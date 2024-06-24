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

@Slf4j  // 로그 작성을 위한 lombok
@RequiredArgsConstructor    // 생성자 자동 생성
@Component  // spring Bean에 등록
public class TokenProvider {    // JWT의 생성, 검증, 인증 정보를 추출
    private final MemberRepository memberRepository;

    @Value("${token.expire.time}")  // 외부에서 정의된 값(일반적으로 yml파일과 같은 환경 설정파일에서)을 spring bean에 주입
    private String tokenExpireTime; // 토큰의 만료 시간

    @Value("${jwt.secret}")
    private String secret;  // jwt 토큰을 서명하고 검증하기 위한 비밀 키
    private Key key;    // jwt 토큰을 서명하고 검증하기 위한 객체 Key

    @PostConstruct  // Bean이 초기화 된 후에 실행
    public void init() {    // 필터 객체를 초기화하고 서비스에 추가하기 위한 메소드 init
        byte[] key = Decoders.BASE64URL.decode(secret);     // 생성자가 실행된 이후 시크릿 키를 base64url로 디코딩 후
        this.key = Keys.hmacShaKeyFor(key);     // hmacsha 알고리즘으로 키 암호화
    }

    // 토큰 생성
    public String generateToken(String email) { // 이메일을 통해 토큰 생성
        Date date = new Date();     // 현재 시간 받아오기
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime));

        return Jwts.builder()   // 권한 정보와 현재 시간을 기반으로 jwt.builder를 통해 토큰 생성
                .setSubject(email)  // 토큰 주체를 email로 설정
                .setIssuedAt(date)  // 토큰 발행 시간 설정
                .setExpiration(expiryDate)  // 토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512)    // 토큰화 해시 알고리즘 HS512 사용하여 서명
                .compact(); // 압축, 서명 후 토큰 생성
    }

    // 토큰 검증(검증 완료되면 true, try catch문을 통해 검증)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)     // 토큰의 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱, 검증

            return true;    // 검증 완료, 유효한 토큰
        } catch (UnsupportedJwtException | MalformedJwtException exception) {   // jwt-parser을 이용해 claims 객체로 파싱하는 과정에서 발생할 수 있는 예외 처리
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
        }

    }

    // 토큰을 통해 인증 정보를 생성(입력 받은 토큰을 파싱하여 claims 객체 얻을 수 있음
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()    // jwt 토큰 파싱
                .setSigningKey(key) // 토큰의 서명 키 설정
                .build()
                .parseClaimsJws(token)  // 토큰 파싱
                .getBody();

        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow();    // 이메일로 회원 조회
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString()));
        // role_user, role_admin
        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities); // authorities를 claims 객체를 통해 추출한 후, usernamepassword~ 로 만들어 리턴
    }

}
