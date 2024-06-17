package org.likelion.likelionjwtlogin.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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

@Slf4j //로깅:정보를 제공하는 일련의 기록, 레이어:계층, 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음이다
@Component //component 어노테이션은 빈 등록 자체를 빈 클래스에다가 할수 있다
@RequiredArgsConstructor //생성자 주입
public class TokenProvider { //토큰 제공자 클래스를 정의함
    private final MemberRepository memberRepository; //memberrepository를 주입함

    @Value("${token.expire.time}") //이 어노테이션은 spring framework에서 제공하는 기능으로 외부에서 정의된값을 주입함
    private String tokenExpireTime; //토큰 만료시간을 저장할 변수

    @Value("${jwt.secret}") //JWT: JSON 객체를 사용하여 정보를 전달, jwt.secret 값을 주입
    private String secret; //JWT 시크릿 키를 저장할 변수
    private Key key; //JWT 서명을 위한 키

    @PostConstruct //객체 생성 후 초기화 작업을 수행하는 메서드에 붙이는 애노테이션
    public void init(){ //초기화 메서드
        byte[] key = Decoders.BASE64URL.decode(secret); //Base64로 인코딩된 시크릿 키를 디코딩(인코딩은 데이터 압축, 다른 형식으로 변환하여 저장 공간 절약, 전송 시간을 줄이는 데 도움이 된다. 디코딩은 인코딩된 데이터를 원래의 형태로 되돌려 사용자가 이해할 수 있게 만드는 역할을 한다.)
        this.key = Keys.hmacShaKeyFor(key); //디코딩된 바이트 배열로 HMAC SHA 키 생성
    }

    public String generateToken(String email) { //JWT 토큰을 생성하는 메서드
        Date date = new Date(); //현재 시간을 나타내는 Date 객체 생성
        Date expireDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime)); //만료 시간을 현재 시간에 더하여 생성

        return Jwts.builder() //JWT 빌더를 사용하여 토큰 생성
                .setSubject(email) //이메일을 주제로 설정
                .setIssuedAt(date)  //토큰 발급 시간 설정
                .setExpiration(expireDate) //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512) //키와 서명 알고리즘 설정
                .compact(); //JWT 토큰 생성 후 압축하여 반환


    }
    public boolean validateToken(String token) { //JWT 토큰을 검증하는 메서드
        try {
            Jwts.parserBuilder()  // JWT 파서 빌더 생성
                    .setSigningKey(key) //서명 키 설정
                    .build()
                    .parseClaimsJws(token); //뭐야 이건 하

            return true; //토큰이 유효한 경우 true 반환
        } catch (UnsupportedJwtException | MalformedJwtException exception) { //지원되지 않거나 잘못된 JWT 예외 처리
            log.error("JWT가 유효하지 않습니다."); //로그(log)란 프로그램 또는 시스템에서 발생하는 이벤트, 정보, 상태, 오류 등을 기록한 것을 말함 , 로그에 에러메세지 출력
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다."); //커스텀 인증예외 발생
        } catch (SignatureException exception) { //서명 검증 실패 예외 처리
            log.error("JWT 서명 검증에 실패했습니다."); //로그에 에러메세지 출력
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다."); //커스텀 인증예외 발생
        } catch (ExpiredJwtException exception) { //만료되 jwt 예외 처리
            log.error("JWT가 만료되었습니다."); //로그에 에러메세지 출력
            throw new CustomAuthenticationException("JWT가 만료되었습니다."); //커스텀 인증 예외 발생
        } catch (IllegalArgumentException exception) { //잘못된 인자 예외 처리
            log.error("JWT가 null이거나 비어 있거나 공백만 있습니다."); //로그에 에러 메세지 출력
            throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다."); //커스텀 인증 예외 발생
        } catch (Exception exception) { //기타 예외 처리
            log.error("JWT 검증에 실패했습니다.", exception); //로그에 에러 메세지 출력
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다."); //커스텀 인증 예외 발생
        }

    }

    public Authentication getAuthentication(String token) { //jwt 토큰으로부터 인증 객체를 가져오는 메서드
        Claims claims = Jwts.parserBuilder() //jwt 파서 빌더 생성
                .setSigningKey(key) //서명 키 설정
                .build()
                .parseClaimsJws(token)//jwt 토큰의 클레임 파싱
                .getBody();

        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(); //이메일로 멤버를 조회
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString())); //멤버의 권한을 리스트로 생성

        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities); //인증객체 생성 후 반환
    }

}
