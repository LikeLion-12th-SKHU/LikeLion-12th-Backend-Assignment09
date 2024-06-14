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

// 로그를 작성할 수 있는 로그 객체를 자동으로 생성한다.
@Slf4j
// final이 붙은 필드의 생성자를 자동 생성해준다.
@RequiredArgsConstructor
// Spring Bean에 등록
@Component
// JWT의 생성, 검증, 인증정보를 추출하는 역할을 한다.
public class TokenProvider {
    // 멤버 정보를 조회하는 데 사용되는 객체를 주입받는다.
    private final MemberRepository memberRepository;

    // 토큰의 만료 시간
    // 해당 필드에 붙은 @Value 어노테이션은 Spring Framework에서 제공하는 기능으로, 외부에서 정의된 값을 Spring Bean에 주입하는데 사용된다.
    @Value("${token.expire.time}")
    private String tokenExpireTime;

    // JWT 토큰을 서명하고 검증하기 위한 비밀 키
    @Value("${jwt.secret}")
    private String secret;

    // JWT 토큰을 서명하고 검증하기 위한 Key 객체
    private Key key;

    // Bean 초기화 후에 실행되는 메서드로, JWT 서명에 사용될 비밀 키를 초기화한다.
    @PostConstruct
    public void init() {
        // 설정된 secret 값을 BASE64URL 디코딩하여 key 초기화
        byte[] key = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(key);
    }

    // 주어진 이메일을 기반으로 JWT 토큰을 생성하여 반환한다.
    public String generateToken(String email) {
        // 현재 시간을 가져온다.
        Date date = new Date();
        // 토큰 만료 시간을 계산. 현재 시간에 토큰 만료 시간을 더한다.
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime));

        // JWT 토큰을 생성하여 반환
        return Jwts.builder()
                .setSubject(email) // 토큰의 주체를 이메일로 설정
                .setIssuedAt(date) // 토큰 발급 시간을 현재 시간으로 설정
                .setExpiration(expiryDate) // 토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512) // 비밀 키와 HS512 알고리즘을 사용하여 토큰에 서명
                .compact(); // 토큰을 압축하여 문자열로 반환
    }

    // 주어진 토큰의 유효성을 검사하고, 유효한 경우 true 반환
    public boolean validateToken(String token) {
        try {
            // 토큰 유효성 검사 및 파싱
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            // JWT 형식이 잘못된 경우
            log.error("JWT가 유효하지 않습니다.");
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다.");
        } catch (SignatureException exception) {
            // JWT 서명 검증에 실패한 경우
            log.error("JWT 서명 검증에 실패했습니다.");
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다.");
        } catch (ExpiredJwtException exception) {
            // JWT 가 만료된 경우
            log.error("JWT가 만료되었습니다.");
            throw new CustomAuthenticationException("JWT가 만료되었습니다.");
        } catch (IllegalArgumentException exception) {
            // JWT 가 null이거나 비어 있거나 공백만 있는 경우
            log.error("JWT가 null이거나 비어 있거나 공백만 있습니다.");
            throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다.");
        } catch (Exception exception) {
            // 기타 예외가 발생한 경우
            log.error("JWT 검증에 실패했습니다.", exception);
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다.");
        }
    }

    // 주어진 JWT 토큰에서 인증 정보를 추출하여 반환한다.
    public Authentication getAuthentication(String token) {
        // 토큰을 파싱하여 Claims 객체를 얻는다.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Claims에서 추출한 이메일을 기반으로 멤버 정보를 조회한다.
        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow();

        // 멤버의 권한 정보를 기반으로 인증 객체를 생성하여 반환한다.
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString()));
        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities);
    }
}
