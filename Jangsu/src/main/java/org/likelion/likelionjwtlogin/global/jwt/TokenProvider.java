// JWT 의 생성, 검증, 인증정보를 추출하는 역할을 한다.

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
import org.likelion.likelionjwtlogin.member.domain.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j                              // 로그를 작성하기 위한 LOMBOK 어노테이션
@RequiredArgsConstructor            // final 이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해주는 롬복 어노테이션
@Component                          // Bean 이 초기화 된 이후에 실행됨
public class TokenProvider {
    private final MemberRepository memberRepository;

    @Value("${token.expire.time}")
    private String tokenExpireTime; // 토큰의 만료시간

    @Value("${jwt.secret}")
    private String secret;          // JWT 토큰을 서명하고 검증하기 위한 비밀키
    private Key key;                // JWT 토큰을 서명하고 검증하기 위한 Key 객체

    @PostConstruct                  // Bean을 초기화 한 후 한번만 실행되는 어노테이션
    public void init() {
        // 비밀키를 BASE64 형태로 복호화
        byte[] key = Decoders.BASE64URL.decode(secret);
        // 의존성 주입
        this.key = Keys.hmacShaKeyFor(key);
    }

    // 토큰을 생성하는 부분(이메일을 매개변수로 받았다.)
    public String generateToken(String email) {

        // new Date()는 힙에 Date(1) 객체를 생성하고 data 변수는 Date(1) 의 메모리 주소를 참조함(상대참조)
        Date date = new Date();
        // new Date()는 힙에 Date(2) 객체를 생성(위와 다른 주소)하고 date 변수는 Date 의 다른 메모리 주소를 참조함.
        // 현재 시간과 long 으로 변환한 토큰 만료시간을 더하여 Date(2)에 저장한다.
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime));

        // 토큰을 반환하는 부분
        return Jwts.builder()
                // 생성 제목을 이메일로 정의
                .setSubject(email)
                // 현재 날짜를 생성 날짜로 정의
                .setIssuedAt(date)
                // 토큰의 만료시간을 정의
                .setExpiration(expiryDate)
                // 키를 HS512로 암호화하는 부분
                .signWith(key, SignatureAlgorithm.HS512)
                .compact(); // 압축
        // HS512 : JWT 가 지원하는 대칭키 암호화 알고리즘
    }

    // 토큰을 검증하는 부분(토큰을 매개변수로 받았다.)
    public boolean validateToken(String token) {
        // 토큰을 검증하는 부분
        try {
            Jwts.parserBuilder() // 토큰을 정수형으로 변환
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            // 성공 시 true 반환
            return true;

        // 실패 시 반환하는 예외에 따라 다르게 실행됨
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            log.error("JWT 가 유효하지 않습니다.");
            throw new CustomAuthenticationException("JWT 가 유효하지 않습니다");
        } catch (SignatureException exception) {
            log.error("JWT 서명 검증에 실패했습니다.");
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다.");
        } catch (ExpiredJwtException exception){
            log.error("JWT 가 만료되었습니다.");
            throw new CustomAuthenticationException("JWT 가 만료되었습니다.");
        } catch (IllegalArgumentException exception){
            log.error("JWT 가 null 이거나 비어있거나 공백만 있습니다.");
            throw new CustomAuthenticationException("JWT 가 null 이거나 비어있거나 공백만 있습니다.");
        } catch (Exception exception){
            log.error("JWT 검증에 실패했습니다.", exception);
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다.");
        }
    }
// 질문. if - else if문 으로도 사용할 수 있을 것 같은데 try - catch 문을 사용한 이유가 궁금합니다


    // 접근을 인증하는 부분
    public Authentication getAuthentication(String token) {
        // 요청을 받는 부분
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // 이메일을 입력받아 레포지토리에서 findBy 함수로 찾는다.
        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow();
        // 권한이나 역할의 이름을 반환하는 메서드를 사용하여 member.getRole()를 문자열로 만들어 리턴함(배열)
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString()));

        // 사용자의 이메일과 공백, authorities 를 반환함
        return new UsernamePasswordAuthenticationToken(member.getEmail(), "",authorities);
    }
}
