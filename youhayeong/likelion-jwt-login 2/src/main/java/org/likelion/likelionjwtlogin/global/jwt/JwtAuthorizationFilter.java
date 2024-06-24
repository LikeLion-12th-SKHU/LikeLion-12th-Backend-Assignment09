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

@Component  // spring Bean에 등록
@RequiredArgsConstructor    // 생성자 자동 생성
public class JwtAuthorizationFilter extends OncePerRequestFilter {  // jwt 유효성 검증
    private static final String AUTHORIZATION_HEADER = "Authorization"; // authorization 헤더를 상수로 선언
    private static final String BEARER_PREFIX = "Bearer ";  // 토큰 앞 접두사 Bearer를 상수로 선언

    private final TokenProvider tokenProvider;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Override   // HTTP 요청에 대한 필터링
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);   // 요청(HttpServletRequest)에서 토큰 추출

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) { // 토큰이 존재하고 유효하다면
                Authentication authentication = tokenProvider.getAuthentication(token); // 토큰으로부터 인증 객체 authentication 생성
                SecurityContextHolder.getContext().setAuthentication(authentication);   // SecurityContextHolder에 인증 객체 설정
            }

            filterChain.doFilter(request, response);    // 필터 실행
        } catch (CustomAuthenticationException e) { // 예외 발생시
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage()));
        }

    }

    private String resolveToken(HttpServletRequest request) {   // 요청에서 토큰 추출
        String token = request.getHeader(AUTHORIZATION_HEADER); // authorization 헤더에서 토큰 값 가져오기
        // Bearer ey ~~
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {   // 토큰 값이 존재하고 Bearer로 시작한다면
            return token.substring(BEARER_PREFIX.length()); // Bearer 접두사를 제외한 실제 토큰 값 리턴
        }

        return null;    // 유효한 토큰 값이 없을 경우 null 리턴
    }
}
