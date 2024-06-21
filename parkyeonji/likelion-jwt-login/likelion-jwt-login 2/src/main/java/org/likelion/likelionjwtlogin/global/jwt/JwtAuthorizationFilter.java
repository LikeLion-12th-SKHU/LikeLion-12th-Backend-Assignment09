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
//JWT 인증 필터 클래스 정의
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    //HTTP 요청 헤더에서 JWT 토큰을 찾기 위해 사용되는 헤더와 접두사 정의
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider; //JWT 토큰을 생성하고 검증하기 위해 객체를 주입 받음
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; //인증 실패 시 호출되는 핸들러

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request); //JWT 토큰 추출하는 메서드 호출

            // 토큰이 유효한지 확인
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token); //토큰에서 인증 정보를 가져옴
                SecurityContextHolder.getContext().setAuthentication(authentication); //인증 정보를 현재 보안 컨텍스트에 설정
            }

            filterChain.doFilter(request, response); //다음 필터 호출
        } catch (CustomAuthenticationException e) { //인증 예외 처리
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage()));
        }

    }

    // 토큰 추출 메서드
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER); //요청 헤더에서 토큰을 가져옴

        //Bearer eyJhb~~~~~~~~~~
        //토큰이 Bearer로 시작하는지 확인
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length()); //접두사를 제거한 토큰 값 반환
        }

        return null; //토큰이 없거나 올바른 형식이 아니면 null 반환
    }
}
