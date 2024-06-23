package org.likelion.likelionjwtlogin.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
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

import java.security.Key;
import java.util.Date;
import java.util.List;
@Slf4j // 로깅 기능 추가
@RequiredArgsConstructor
@Component
public class TokenProvider {
    private final MemberRepository memberRepository;
    @Value("${token.expire.time}") // 토큰 만료 시간 주입
    private String tokenExpireTime; // 토큰 만료 시간 필드
    @Value("${jwt.secret}") // JWT 비밀 키 값 주입
    private String secret; // 비밀 키 필드
    private Key key; // 키 객체
    @PostConstruct
    public void init() {
        byte[] key = Decoders.BASE64URL.decode(secret); // 비밀 키 디코딩
        this.key = Keys.hmacShaKeyFor(key);
    }
    public String generateToken(String email) {
        Date date = new Date(); // 현재 날짜 객체 생성
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime)); // 만료 날짜를 계산
        return Jwts.builder() // JWT 빌더 시작
                .setSubject(email) // 이메일 설정
                .setIssuedAt(date) // 발행 날짜 설정
                .setExpiration(expiryDate) // 만료 날짜 설정
                .signWith(key, SignatureAlgorithm.HS512) // 서명 알고리즘과 키 설정
                .compact(); // JWT생성
    }
    public boolean validateToken(String token) { // 토큰 검증 메서드
        try {
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token);
                return true; // 검증 성공하면 true
        } catch (UnsupportedJwtException | MalformedJwtException exception) { // 예외 처리
            log.error("JWT가 유효하지 않습니다."); // 로그 출력
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다."); // 커스텀 예외 발생
        } catch (SignatureException exception) { // 서명 예외 처리
            log.error("JWT 서명 검증에 실패했습니다.");
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다."); // 커스텀 예외 발생
        } catch (ExpiredJwtException exception) { // 만료된 JWT 예외 처리
            log.error("JWT가 만료되었습니다.");
            throw new CustomAuthenticationException("JWT가 null이거나 비어있거나 공백만 있습니다."); // 커스텀 예외 발생
        } catch (IllegalArgumentException exception) { // 잘못된 인자 예외 처리
            log.error("JWT가 null이거나 비어있거나 공백만 있습니다.");
            throw new CustomAuthenticationException("JWT가 null이거나 비어있거나 공백만 있습니다."); // 커스텀 예외 발생
        } catch (Exception exception) { // 나머지 예외 처리
            log.error("JWT 검증에 실패했습니다.", exception);
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다."); // 커스텀 예외 발생
        }
    }
    public Authentication getAuthentication(String token) { // 인증 객체 반환 메서드
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(); // 이메일로 회원 조회
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString())); // 권한 리스트

        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities); // 인증 객체 생성 및 반환
    }
}