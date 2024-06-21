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

@Slf4j //로깅 기능을 쉽게 추가할 수 있도록 하는 Lombok 어노테이션.
@RequiredArgsConstructor
@Component
public class TokenProvider {
    private final MemberRepository memberRepository;

    @Value("${token.expire.time}") //@Value: 외부에서 정의된 값을 Spring Bean에 주입.
    private String tokenExpireTime; //토큰의 만료 시간

    @Value("${jwt.secret}")
    private String secret; //JWT 토큰을 서명하고 검증하기 위한 비밀 키
    private Key key; //JWT 토큰을 서명하고 검증하기 위한 Key 객체

    @PostConstruct //Bean이 초기화 된 후에 실행됨.
    // 키 객체 설정
    public void init() {
        byte[] key = Decoders.BASE64URL.decode(secret); //secret 값을 Base64URL 형식으로 디코딩.
        this.key = Keys.hmacShaKeyFor(key); //디코딩된 key를 사용하여 HMAC SHA 알고리즘에 사용할 키 객체를 생성하고 key 필드에 저장.
    }

    //JWT 토큰 생성
    public String generateToken(String email) {
        Date date = new Date(); // 현재 시간 설정
        Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime)); // 현재 시간에 만료시간을 더해 만료 되는 시간 설정

        return Jwts.builder()
                .setSubject(email) //토큰의 사용자를 email로 설정
                .setIssuedAt(date) //토큰의 발행 시간을 현재 시간으로 설정
                .setExpiration(expiryDate) //토큰의 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS512) //토큰에 서명
                .compact(); //JWT 토큰을 생성하고 문자열 형태로 반환
    }

    // 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try { // 파싱 및 검증
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (UnsupportedJwtException | MalformedJwtException exception) { //JWT가 지원되지 않는/올바르지 않은 형식인 경우
            log.error("JWT가 유효하지 않습니다.");
            throw new CustomAuthenticationException("JWT가 유효하지 않습니다.");
        } catch (SignatureException exception) { //서명 검증 실패한 경우
            log.error("JWT 서명 검증에 실패했습니다.");
            throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다.");
        } catch (ExpiredJwtException exception) { //JWT가 만료된 경우
            log.error("JWT가 만료되었습니다.");
            throw new CustomAuthenticationException("JWT가 만료되었습니다.");
        } catch (IllegalArgumentException exception) { //JWT 문자열이 null 혹은 비어있거나 공백만 있는 경우
            log.error("JWT가 null이거나 비어 있거나 공백만 있습니다.");
            throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다.");
        } catch (Exception exception) { //그 외 모든 경우
            log.error("JWT 검증에 실패했습니다.", exception);
            throw new CustomAuthenticationException("JWT 검증에 실패했습니다.");
        }

    }

    // 사용자 인증 정보 생성
    public Authentication getAuthentication(String token) { //Authentication: 인증 정보를 나타내는 인터페이스
        // JWT 토큰 파싱 및 클레임 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody(); //페이로드 부분에 추출한 클레임을 가져옴.

        Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(); //회원 정보 조회
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString())); //권한 설정
        //GrantedAuthority: 사용자의 권한을 나타내는 인터페이스
        return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities); //인증 정보를 생성하여 반환
    }

}
