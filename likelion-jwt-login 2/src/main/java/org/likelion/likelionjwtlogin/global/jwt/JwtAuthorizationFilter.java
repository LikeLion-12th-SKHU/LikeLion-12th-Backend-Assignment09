package org.likelion.likelionjwtlogin.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.likelion.likelionjwtlogin.global.error.CustomAuthenticationFailureHandler;
import org.likelion.likelionjwtlogin.global.jwt.exception.CustomAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
	private static final String AUTHORIZATION_HEADER = "Authorization"; //인가 헤더.
	private static final String BEARER_PREFIX = "Bearer "; //인가 헤더 적을 때 붙여주는 전치사 "Bearer ".

	private final TokenProvider tokenProvider; //jwt 관련 토큰로직을 다루는 클래스 주입.
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; //사용자 정의 인증 실패 핸들러 주입.

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) //실제 필터체인
		throws ServletException, IOException {

		try {
			String token = resolveToken(request); //서블릿 요청에서 토큰을 똑 떼어서 저장.

			if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) { //토큰이 있고, 유효하면
				Authentication authentication = tokenProvider.getAuthentication(token); // 인증절차를 거쳐서 인증
				SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContextHolder는 내부에 securityContext를 가지고 있고 그 내부에 authenticaion을 관리한다. 따라서 그 context의 authentication객체를 인증 성공해서 받아온 애를 넣어준다.
			}

			filterChain.doFilter(request, response); //이제 필터체인에 등록해둔 대로 필터과정을 실행시킨다.
		} catch (CustomAuthenticationException e) { //만약 설정해둔 사용자 정의 인증 예외가 발생하면
			customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage())); //커스텀 예외를 customAuthenticationFailureHandler를 이용하여 던진다.
		}

	}

	private String resolveToken(HttpServletRequest request) { //서블릿 요청을 받아서 토큰을 해석하는 부분
		String token = request.getHeader(AUTHORIZATION_HEADER); //헤더에서 Authorization에 해당하는 부분을 가져옴. "Bearer 토큰내용어쩌구"

		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) { //토큰내용이 있고, 전치사로 시작하는 문자열을 가져와서
			return token.substring(BEARER_PREFIX.length()); // 토큰내용만 짤라서 리턴.
		}

		return null; //아니면 Null로 보냄.
	}
}
