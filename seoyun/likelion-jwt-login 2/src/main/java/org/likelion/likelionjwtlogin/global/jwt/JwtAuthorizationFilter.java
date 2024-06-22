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
@RequiredArgsConstructor //초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성함
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization"; // AUTHORIZATION_HEADER 정의, HTTP 요청 헤더에서 인증 정보를 나타냄
    private static final String BEARER_PREFIX = "Bearer "; //BEARER_PREFIX는 인증 타입을 의미

    private final TokenProvider tokenProvider; //TokenProvider 클래스 타입의 tokenProvider 필드를 선언하고 초기화
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; //CustomAuthenticationFailureHandler 클래스 타입의 customAuthenticationFailureHandler 필드를 선언하고 초기화

    //HttpServletRequest를 통해 요청한 URL, HTTP 메소드(GET, POST 등), 쿠키, 헤더 정보, 요청 파라미터 등의 정보를 받음
    //HttpServletResponse는 요청에 대한 응답을 생성하는 역할을 맡은 타입
    //Filterchain은 연결된 다음 filter를 호출
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } //StringUtils.hasText(token), tokenProvider.validateToken(token) 둘다 있으면 Authentication~setAuthentication(authentication); 코드 실행

            filterChain.doFilter(request, response);
        } catch (CustomAuthenticationException e) {
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage()));
        } //customAuthenticationFailureHandler: Spring Security에서 사용자의 인증이 실패했을 때의 처리를 사용자가 직접 정의한 핸들러

    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length()); //BEARER_PREFIX의 길이를 반환
        }

        return null;
    }
}
