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

// 로그를 쉽게 사용할 수 있게 해준다.
@Slf4j
// 컨트롤러에서 발생하는 예외를 처리하기 위한 클래스를 정의한다.
@RequiredArgsConstructor
@Component // Spring Bean에 등록한다.
public class TokenProvider {
    private final MemberRepository memberRepository;

    // 토큰의 만료 시간
    // 해당 필드에 붙은 @Value 어노테이션은 Spring Framework에서 제공하는 기능으로, 외부에서 정의된 값을 Spring Bean에 주입하는데 사용
    //이 값은 일반적으로 application.yml 파일과 같은 환경 설정 파일에서 가져온다.
    @Value("${token.expire.time}")
    private String tokenExpireTime;

    @Value("${jwt.secret}")
    private String secret;
    // JWT 토큰을 서명하고 검증하기 위한 Key 객체이다.
    private Key key;

    // Bean이 초기화 된 후에 시행
    @PostConstruct
    public void init() {
        // 비밀키를 BASE64TURL 형식으로 디코딩하여 키 객체를 초기화
        byte[] key = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(key);
    }

    // JWT 토큰을 생성하는 메서드
    public String generateToken(String email) {
        Date date = new Date();
        // 현재 시간에 만료 시간을 더한다.
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime));

        // JWT 토큰을 생성하여 반환
        return Jwts.builder()
                .setSubject(email) // 토큰의 사용자 이메일 설정
                .setIssuedAt(date) // 토큰 발급 시간을 현재시간으로 설정
                .setExpiration(expiryDate) // 토콘 만료 시간을 설정
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT 토큰의 유효성을 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱하고 유효성을 검사한다.
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            // 유효하면 true 반환
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            // JWT 유효 검증 실패
            log.error("JWT가 유효하지 않습니다.");
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다.");
        } catch (SignatureException exception) {
            // JWT 서명 검증 실패
            log.error("JWT 서명 검증에 실패했습니다.");
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다.");
        } catch (ExpiredJwtException exception) {
            // JWT 만료 예외 처리
            log.error("JWT가 만료되었습니다.");
            throw new CustomAuthenticationException("JWT가 만료되었습니다.");
        } catch (IllegalArgumentException exception) {
            // JWT가 null, 공백이면 예외 처리
            log.error("JWT가 null이거나 비어 있거나 공백만 있습니다.");
            throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다.");
        } catch (Exception exception) {
            // 기타 예외 처리
            log.error("JWT 검증에 실패했습니다.", exception);
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다.");
        }

    }

    // JWT 토큰에서 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        // 토큰을 파싱하여 Claims를 가져온다.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Claims에서 이메일을 사용하여 회원 정보를 조회한다.
        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow();
        // 회원 권한을 설정하고
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString()));
        // 인증 객체를 생성하여 반환한다.
        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities);
    }

}
