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

@Component // 스프링 컴포넌트 스캔에 포함시켜 스프링 빈으로 등록
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동 생성
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization"; // HTTP 요청 헤더에서 토큰을 찾기 위한 헤더
    private static final String BEARER_PREFIX = "Bearer "; // JWT 토큰의 접두사

    private final TokenProvider tokenProvider; // 토큰을 제공하는 클래스
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; // 인증 실패 핸들러

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request); // 요청 헤더에서 토큰 추출

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) { // 토큰이 유효한 경우
                Authentication authentication = tokenProvider.getAuthentication(token); // 토큰에서부터 인증 정보 생성
                SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보 저장
            }

            filterChain.doFilter(request, response); // 다음 필터로 요청과 응답 전달
        } catch (CustomAuthenticationException e) {
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage())); // 예외 발생 시 에러 응답 전송
        }

    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER); // 헤더에서 토큰 추출

        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) { // 토큰이 존재하고 bearer 접두사로 시작하는지 확인
            return token.substring(BEARER_PREFIX.length()); // 접두사 제거 후 토큰 반환
        }

        return null; // 유효 토큰이 없다면 null 반환
    }
}
