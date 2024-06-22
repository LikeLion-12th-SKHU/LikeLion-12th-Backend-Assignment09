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

@Slf4j // 로그를 작성하기 위한 lombok 어노테이션
@RequiredArgsConstructor
@Component //spring bean에 등록
public class TokenProvider {
    private final MemberRepository memberRepository;

    //vlaue 어노테이션은 spring framework에서 제공하는 기능, 외부에서 정의되는 값을 spring bean에 주입하는데 사용
    @Value("${token.expire.time}")
    private String tokenExpireTime;

    @Value("${jwt.secret}")
    private String secret; //JWT 토큰을 서명하고 검증하기 위한 비밀 키
    private Key key; //JWT 토큰을 서명하고 검증하기 위한 key 객체

    @PostConstruct //bean이 초기화 된 후에 실행
    public void init() {
        byte[] key = Decoders.BASE64URL.decode(secret); //secret 문자열을 URL-safe Base64 형식에서 디코딩
        this.key = Keys.hmacShaKeyFor(key);
    }

    public String generateToken(String email) {
        Date date = new Date(); //Date 생성자는 시간의 특정 지점을 나타내는 Date 객체를 플랫폼에 종속되지 않는 형태로 생성
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime)); //현재 시간에 tokenExpireTime을 더하여 만료 날짜를 설정

        return Jwts.builder() //토큰 생성하여 반환
                .setSubject(email) //메세지 제목 지정
                .setIssuedAt(date) //날짜 생성
                .setExpiration(expiryDate) //만료 날짜
                .signWith(key, SignatureAlgorithm.HS512) //HS512 알고리즘 사용
                .compact();
    }

    //validateToken() : 토큰을 검증하는 역할을 수행
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true; //검증이 성공하면 true를 반환
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            log.error("JWT가 유효하지 않습니다.");
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다.");
        } catch (SignatureException exception) {
            log.error("JWT 서명 검증에 실패했습니다.");
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다.");
        } catch (ExpiredJwtException exception) {
            log.error("JWT가 만료되었습니다.");
            throw new CustomAuthenticationException("JWT가 만료되었습니다.");
        } catch (IllegalArgumentException exception) {
            log.error("JWT가 null이거나 비어 있거나 공백만 있습니다.");
            throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다.");
        } catch (Exception exception) {
            log.error("JWT 검증에 실패했습니다.", exception);
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다.");
        } //만약 예외가 발생하면 try 블록을 빠져나와 catch 블록에서 처리

    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) //jwt parse 구성할 때 서명 검증을 위해 사용할 비밀 키를 설정하는 데 사용
                .build()
                .parseClaimsJws(token)
                .getBody();

        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(); //사용자 조회
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString())); //member 권한 설정

        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities); //인증 객체 생성 및 반환
    }

}
