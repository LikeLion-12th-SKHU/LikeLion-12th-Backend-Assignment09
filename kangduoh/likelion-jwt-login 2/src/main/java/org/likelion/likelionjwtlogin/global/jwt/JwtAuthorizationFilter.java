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

// Spring Bean에 등록한다.
@Component
// final이 붙은 필드의 생성자를 자동 생성해준다.
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    // HTTP 요청 헤더에서 토큰을 추출할 때 사용되는 헤더와 Bearer 접두사 정의
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    // 토큰 생성 및 유효성 검사를 담당하는 객체를 주입받는다.
    private final TokenProvider tokenProvider;
    // 사용자 정의 인증 실패 핸들러를 주입받는다.
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    // HTTP 요청에 대한 필터링 작업을 수행하는 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // HTTP 요청에서 토큰 추출
            String token = resolveToken(request);

            // 추출한 토큰이 유효하고, 토큰을 통해 인증을 수행한 경우 SecurityContextHolder 에 인증 정보 설정
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 다음 필터로 요청과 응답을 전달
            filterChain.doFilter(request, response);
        } catch (CustomAuthenticationException e) {
            // 사용자 정의 인증 예외가 발생한 경우 예외 처리
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage()));
        }
    }

    // HTTP 요청에서 토큰을 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        // HTTP 요청 헤더에서 Authorization 헤더 값을 가져온다.
        String token = request.getHeader(AUTHORIZATION_HEADER);

        // Authorization 헤더 값이 존재하고, Bearer 접두사로 시작하는 경우 토큰 값을 반환
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }

        // 토큰이 존재하지 않는 경우 null 반환
        return null;
    }
}
