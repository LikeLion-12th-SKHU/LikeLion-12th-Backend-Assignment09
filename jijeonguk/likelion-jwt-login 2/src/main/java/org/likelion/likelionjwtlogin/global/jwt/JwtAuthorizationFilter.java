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
// final 필드에 대한 생성자를 자동으로 생성해준다.
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    // HTTP 헤더에서 Authorization 헤더의 이름을 정의한다.
    private static final String AUTHORIZATION_HEADER = "Authorization";
    // Authorization 헤더 값의 접두사를 정의한다.
    private static final String BEARER_PREFIX = "Bearer ";

    
    private final TokenProvider tokenProvider;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    // HTTP 요청에 대한 필터링을 수행하는 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // HTTP 요청에서 토큰을 추출
            String token = resolveToken(request);

            // 토큰이 유효한지 확인하고, 유효한 경우 SecurityContextHolder에 인증 정보를 설정
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 다음 필터를 호출
            filterChain.doFilter(request, response);
            //예외가 발생하면 처리한다
        } catch (CustomAuthenticationException e) {
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage()));
        }

    }

    // HTTP 요청에서 토큰을 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        // HTTP 요청 Authorizaion 헤더값을 가져온다.
        String token = request.getHeader(AUTHORIZATION_HEADER);

        // 토큰이 존재하고, Bearer 접두사로 시적하면 접두사를 제거한 토큰을 반환
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }

        // 토큰이 존재하지 않으면 null 반환
        return null;
    }
}
