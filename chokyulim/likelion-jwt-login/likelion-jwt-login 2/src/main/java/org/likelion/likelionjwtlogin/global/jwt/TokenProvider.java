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

@Slf4j // Lombok 어노테이션으로, 로그를 사용하기 위한 설정입니다.
@RequiredArgsConstructor // Lombok 어노테이션으로, final 필드에 대한 생성자를 자동으로 생성합니다.
@Component // 이 클래스를 스프링 빈으로 등록하여 스프링 컨텍스트에서 관리할 수 있도록 합니다.
public class TokenProvider {
    private final MemberRepository memberRepository; // 회원 정보를 조회하기 위한 MemberRepository 주입

    @Value("${token.expire.time}") // 설정 파일에서 토큰 만료 시간을 주입받습니다.
    private String tokenExpireTime;

    @Value("${jwt.secret}") // 설정 파일에서 JWT 비밀키를 주입받습니다.
    private String secret;
    private Key key; // JWT 서명을 검증하기 위한 키

    /**
     * 빈이 초기화된 후 호출되는 메서드로, JWT 서명을 검증하기 위한 키를 초기화합니다.
     */
    @PostConstruct
    public void init() {
        byte[] key = Decoders.BASE64URL.decode(secret); // 비밀키를 Base64URL 형식으로 디코딩
        this.key = Keys.hmacShaKeyFor(key); // HMAC SHA 알고리즘을 사용하여 키 생성
    }

    /**
     * JWT 토큰을 생성하는 메서드
     *
     * @param email 사용자 이메일
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String email) {
        Date date = new Date(); // 현재 시간을 가져옴
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime)); // 현재 시간에 만료 시간을 더해 만료 시간 설정

        return Jwts.builder()
                .setSubject(email) // 토큰의 주체를 이메일로 설정
                .setIssuedAt(date) // 토큰 발급 시간 설정
                .setExpiration(expiryDate) // 토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512) // 서명 알고리즘과 키를 사용하여 서명
                .compact(); // 토큰을 생성하여 반환
    }

    /**
     * JWT 토큰을 검증하는 메서드
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효한 경우 true, 그렇지 않은 경우 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 토큰 서명을 검증하기 위한 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰을 파싱하고 서명을 검증

            return true; // 토큰이 유효한 경우 true 반환
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            log.error("JWT가 유효하지 않습니다."); // 로그를 통해 예외 메시지 출력
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다."); // 커스텀 예외 발생
        } catch (SignatureException exception) {
            log.error("JWT 서명 검증에 실패했습니다."); // 로그를 통해 예외 메시지 출력
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다."); // 커스텀 예외 발생
        } catch (ExpiredJwtException exception) {
            log.error("JWT가 만료되었습니다."); // 로그를 통해 예외 메시지 출력
            throw new CustomAuthenticationException("JWT가 만료되었습니다."); // 커스텀 예외 발생
        } catch (IllegalArgumentException exception) {
            log.error("JWT가 null이거나 비어 있거나 공백만 있습니다."); // 로그를 통해 예외 메시지 출력
            throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다."); // 커스텀 예외 발생
        } catch (Exception exception) {
            log.error("JWT 검증에 실패했습니다.", exception); // 로그를 통해 예외 메시지 출력
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다."); // 커스텀 예외 발생
        }
    }

    /**
     * JWT 토큰에서 인증 정보를 추출하는 메서드
     *
     * @param token JWT 토큰
     * @return 인증 정보가 포함된 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // 서명을 검증하기 위한 키 설정
                .build()
                .parseClaimsJws(token) // 토큰을 파싱하여 클레임을 가져옴
                .getBody();

        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(); // 클레임에서 이메일을 추출하여 회원 정보를 조회
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString())); // 회원의 권한을 설정

        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities); // 인증 정보를 포함한 Authentication 객체를 생성하여 반환
    }
}
