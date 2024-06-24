package org.likelion.likelionjwtlogin.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.likelion.likelionjwtlogin.global.jwt.exception.CustomAuthenticationException;
import org.likelion.likelionjwtlogin.member.domain.Member;
import org.likelion.likelionjwtlogin.member.domain.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {
    private final MemberRepository memberRepository; // 멤버 레포지토리 의존성 주입

    @Value("${token.expire.time}") // 토큰 만료 시간 주입
    private String tokenExpireTime;

    @Value("${jwt.secret}") // JWT 비밀키 값 주입
    private String secret; // 서명을 위한 비밀 키
    private Key key; // 서명에 사용될 key 객체

    @PostConstruct
    public void init() { // 비밀키를 디코딩하여 키 객체 초기화
        byte[] key = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(key);
    }

    public String generateToken(String email) { // 이메일을 사용해 JWT 토큰 생성
        Date date = new Date(); // 현재 시간
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime)); // 만료 시간

        return Jwts.builder() // JWT 토큰을 생성하고 반환
                .setSubject(email)
                .setIssuedAt(date)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) { // JWT 토큰의 유효성 검사
        try {
            Jwts.parserBuilder() // 파서를 생성하고 토큰 검증
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true; // 성공 시 true 반환
        } catch (UnsupportedJwtException | MalformedJwtException exception) { // 유효하지 않을 경우 예외 발생
            log.error("JWT가 유효하지 않습니다.");
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다.");
        } catch (SignatureException exception) { // 서명 검증 실패 시  예외 발생
            log.error("JWT 서명 검증에 실패했습니다.");
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다.");
        } catch (ExpiredJwtException exception) { // 시간 만료 시 예외 발생
            log.error("JWT가 만료되었습니다.");
            throw new CustomAuthenticationException("JWT가 만료되었습니다.");
        } catch (IllegalArgumentException exception) {
            log.error("JWT가 null이거나 비어 있거나 공백만 있습니다."); // null이거나 공백일 경우 예외 발생
            throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다.");
        } catch (Exception exception) { // 기타 예외가 발생할 경우 예외 발생
            log.error("JWT 검증에 실패했습니다.", exception);
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다.");
        }

    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder() // 파서를 사용해 클레임 추출
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(); // 이메일 추출 후 회원 정보 조회
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString())); // 권한 정보를 바탕으로 인증 객체 생성 후 반환

        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities);
    }

}
