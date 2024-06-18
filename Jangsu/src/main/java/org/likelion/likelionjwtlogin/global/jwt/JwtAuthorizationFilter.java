package org.likelion.likelionjwtlogin.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.likelion.likelionjwtlogin.global.error.CustomAuthenticationFailureHandler;
import org.likelion.likelionjwtlogin.global.jwt.exception.CustomAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 하나 이상의 @Bean 메서드를 가지고 있어 Spring 컨테이너가 해당 메서드들을 관리, Bean 으로 등록한다는 의미
@Component
// final 이 붙거나 @NotNull 이 붙은 필드의 생성자를 자동 생성해 주는 LOMBOK 어노테이션
@RequiredArgsConstructor
// OncePerRequestFilter(부모), JwtAuthorizationFilter(자식)
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    // 베어러는 Basic, Digest 등과 같은 다른 유형의 인증과 구별하기 위해 사용하는 토큰, 나름 역사가 오래되었다.
    private static final String BEARER_PREFIX = "Bearer ";

    // private, 상수형태로 선언된 토큰 공급자
    private final TokenProvider tokenProvider;
    // private, 상수형태로 선언된 customAuthenticationFailureHandler
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // HttpServletRequest 의 헤더 정보에 있는 토큰을 가져오는 역할을 함(현재는 request 에 저장됨) 그것을 token 에 할당
            String token = resolveToken(request);

            // 참인 경우
            // 널인지 길이가 0인지 공백이 있는지를 판단(없으면 참) && 토큰의 유효성 검사
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
            // try 문에서 실패한 경우 반환하는 예외 e
        } catch (CustomAuthenticationException e) {
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage()));
        }
    }

    private String resolveToken(HttpServletRequest request) {
        // 헤더의 이름으로 헤더의 값을 token 에 할당
        String token = request.getHeader(AUTHORIZATION_HEADER);

        // token 이 공백인지 아닌지(아니면 참) && 토큰이 특정 문자("Bearer ")로 시작하는지 판별(
        // 참인 경우 토큰의 BEARER_PREFIX.length() -> 0 ~ 6만큼 날리고 나머지를 반환
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }

        // 거짓인 경우 null 값을 반환
        return null;
    }
}
