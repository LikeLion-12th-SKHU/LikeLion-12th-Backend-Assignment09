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
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter { // OncePerRequestFilter를 상속받는 JWT 인증 필터 클래스 선언
    private static final String AUTHORIZATION_HEADER = "Authorization"; // 헤더 이름 정의
    private static final String BEARER_PREFIX = "Bearer "; // 토큰 정의
    private final TokenProvider tokenProvider; // 토큰 제공자 객체
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; // 커스텀 인증 실패 핸들러
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException { // 필터 내부 로직 구현

        try {
            String token = resolveToken(request); // 요청에서 토큰 추출
            if (StringUtils.hasText(token) && tokenProvider.validateToken (token)) { // 토큰이 유효한지 확인
                Authentication authentication = tokenProvider.getAuthentication(token); // 토큰에서 인증 객체를 생성
                SecurityContextHolder.getContext().setAuthentication(authentication); // 보안 컨텍스트에 인증 객체를 설정
            }
            filterChain.doFilter(request, response); // 다음 필터로 요청과 응답을 전달
        } catch (CustomAuthenticationException e) { // 커스텀 인증 예외가 발생한 경우
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage())); // 커스텀 예외를 핸들러를 통해 처리
        }
    }
    private String resolveToken(HttpServletRequest request) { // 요청에서 토큰을 추출하는 메서드
        String token = request.getHeader(AUTHORIZATION_HEADER); // 요청 헤더에서 토큰을 가져옴

        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) // 토큰 존재 하는지, Bearer 접두사로 시작하는지 확인
        {
            return token.substring(BEARER_PREFIX.length()); // 접두사 버리고 토큰을 반환
        }
        return null; // 유효한 토큰이 없으면 null
    }
}