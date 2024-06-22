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
public class TokenProvider { //jwt관련 토큰 로직 처리.
	private final MemberRepository memberRepository; //멤버 레포지터리 주입

	@Value("${token.expire.time}")
	private String tokenExpireTime; //토큰 만료 시간

	@Value("${jwt.secret}")
	private String secret; //jwt 시크릿 키
	private Key key; //

	@PostConstruct //빈 생성후에 딱한번 실행됨을 보장하여 초기화를 보장해주는 어노테이션으로 알고있다.
	public void init() {
		byte[] key = Decoders.BASE64URL.decode(secret); //jwt를 암호화할때 먼저 헤더의 인코딩값 + 내용 인코딩값을 hex로 바꾸고 base 64로 시크릿키를 통해 암호화하기 때문에 반대로 간다. base64를 시크릿키를 통해 풀고?
		this.key = Keys.hmacShaKeyFor(key); //hex로 디코드함.
	}

	public String generateToken(String email) { //이메일을 받아서 토큰 생성.
		Date date = new Date(); //date객체 생성
		Date expiryDate = new Date(date.getTime() + Long.parseLong(tokenExpireTime)); //현재 date시간 + 만료시간 더해서 만료일 만들기.

		return Jwts.builder() //jwt만들기.
			.setSubject(email) //토큰 제목
			.setIssuedAt(date) //토큰 발급자 등록. 원래는 예로 www.apple.com막 이런식으로 발급자가 들어가야되는걸로 알긴하는데 흠냐 ㅋㅋ date가 드가있네용
			.setExpiration(expiryDate) //만료일 등록
			.signWith(key, SignatureAlgorithm.HS512) //서명하는 키와 알고리즘을 등록.
			.compact(); //jwt를 사실상 실제로 생성하며 selializable한 문자열로 만들어준다.
	}

	public boolean validateToken(String token) { //토큰에 대한 유효성 검사.
		try {
			Jwts.parserBuilder()
				.setSigningKey(key) // 서명해독기 설정.
				.build()// 해당하는 parser로
				.parseClaimsJws(token); //jwt분석하는데 에러 안나면?

			return true; //통과
		} catch (UnsupportedJwtException | MalformedJwtException exception) { //jwt가 형식에 안맞을 때, 그리고 MalformedJwtException은 토큰의 값이 없거나 비정상일때 발생. 예로 null,0,0이런거로 암호화되어있는 맹짜인 경우
			log.error("JWT가 유효하지 않습니다.");
			throw new CustomAuthenticationException("JWT가 유효하지 않습니다."); //커스텀 예외 아래는 생략.
		} catch (SignatureException exception) { //서명 검증 예외
			log.error("JWT 서명 검증에 실패했습니다.");
			throw new CustomAuthenticationException("JWT 서명 검증에 실패했습니다.");
		} catch (ExpiredJwtException exception) { //jwt 만료 예외
			log.error("JWT가 만료되었습니다.");
			throw new CustomAuthenticationException("JWT가 만료되었습니다.");
		} catch (IllegalArgumentException exception) { // 그외 Jwt가 null같은 경우일 때 예외
			log.error("JWT가 null이거나 비어 있거나 공백만 있습니다.");
			throw new CustomAuthenticationException("JWT가 null이거나 비어 있거나 공백만 있습니다.");
		} catch (Exception exception) { //그외 예외
			log.error("JWT 검증에 실패했습니다.", exception);
			throw new CustomAuthenticationException("JWT 검증에 실패했습니다.");
		}

	}

	public Authentication getAuthentication(String token) { //토큰을 받아서 인증을 진행.
		Claims claims = Jwts.parserBuilder()//jwt 파서빌더를 통해 파싱을 하고 해당하는 Claims를 가져온다. Claims는 맵의형태로서 데이터의 정보들이 하나하나 담겨진다.
			.setSigningKey(key)// 키 서명을 설정. 약간 어떤거로 해독할지이다. 이 서명은 헤더의 인코딩값과, 정보의 인코딩값을 합친후 주어진 비밀키로 해쉬를 하여 생성되는데, 암호화 할때 hex로 한번 -> base64로 인코딩 하게 됨. decode니까 반대로
			.build()// 키서명을 토대로 jwtparser를 빌드.
			.parseClaimsJws(token) //토큰에서 해당 parser로 내용들을 파싱.
			.getBody(); //그중 바디를 가져옴.

		Member member = memberRepository.findByEmail(claims.getSubject()).orElseThrow(); //Claims중에는 여러정보들이 있는데 그중 sub는 토큰의 제목을 의미함. 우리는 그 제목에 간단하게 유니크 이메일로 넣어놨다. 그거로 유저찾기.
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole().toString()));//Authentication 객체의 권한 부여 문자열을 저장하는 SimpleGrantedAuthority에 넘겨줘 권한 리스트를 저장하고

		return new UsernamePasswordAuthenticationToken(member.getEmail(), "", authorities); //여기에서 usernamePasswordAuthentication객체를 리턴하게 되는데, 여기서 principal 즉 이메일이 id역할, credential은 비밀번호가 된다. 현재 credential은 ""으로 설정해둔 것을 볼 수 있다.
	}

}
