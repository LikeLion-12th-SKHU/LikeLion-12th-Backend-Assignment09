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

@Component // 이 클래스를 스프링 빈으로 등록하여 스프링 컨텍스트에서 관리할 수 있게 함
@RequiredArgsConstructor // final 필드로 선언된 변수들에 대한 생성자를 자동으로 생성해주는 Lombok 어노테이션
public class JwtAuthorizationFilter extends OncePerRequestFilter { // OncePerRequestFilter 클래스를 상속받아 요청 당 한 번만 실행되는 필터를 구현

    private static final String AUTHORIZATION_HEADER = "Authorization"; // Authorization 헤더를 나타내는 상수
    private static final String BEARER_PREFIX = "Bearer "; // Bearer 접두사를 나타내는 상수

    private final TokenProvider tokenProvider; // 토큰을 생성하고 검증하는 기능을 제공하는 TokenProvider 객체
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; // 인증 실패 시 처리하는 CustomAuthenticationFailureHandler 객체

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException { // 필터의 핵심 로직을 구현하는 메서드, 요청 당 한 번 실행됨
        try {
            String token = resolveToken(request); // 요청 헤더에서 JWT 토큰을 추출

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) { // 토큰이 존재하고 유효한지 검증
                Authentication authentication = tokenProvider.getAuthentication(token); // 토큰에서 인증 정보를 가져옴
                SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 인증 정보 저장
            }

            filterChain.doFilter(request, response); // 다음 필터 체인을 실행
        } catch (CustomAuthenticationException e) { // CustomAuthenticationException 예외가 발생한 경우
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage())); // 인증 실패 처리
        }
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출하는 메서드
     * @param request HttpServletRequest 객체
     * @return 추출한 JWT 토큰 문자열 또는 null
     */
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER); // Authorization 헤더에서 토큰을 가져옴

        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) { // 토큰이 존재하고 Bearer 접두사로 시작하는지 확인
            return token.substring(BEARER_PREFIX.length()); // Bearer 접두사를 제거하고 토큰 반환
        }

        return null; // 유효한 토큰이 없는 경우 null 반환
    }
}
