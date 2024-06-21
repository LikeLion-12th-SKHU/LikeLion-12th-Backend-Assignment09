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
//JwtAuthorizationFilter : 클라이언트 요청이 서버에 도착하기 전에 실행, JWT 토큰 유효성 검사 & 토큰 기반 사용자 인증 수행
//extends OncePerRequestFilter : 요청당 한 번만 필터가 실행되도록
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Override
    //doFilterInternal : 요청 들어올 때마다 실행
    //토큰이 유효하지 않거나 존재하지 않는 경우, customAuthenticationFailureHandler를 통해 예외 발생
    //request, response, filterChain에 주황 줄이 쳐지는 이유는 Null Safety 때문
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = resolveToken(request);

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (CustomAuthenticationException e) {
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage()));
        }

    }

    //JWT 토큰은 실제 토큰 값과 구분하기 위해 접두사 'Bearer'를 사용
    //Bearer? : 토큰은 요청 헤더의 Authorization 필드에 담겨 보내지는데, 이때 Authorization은 <type> <credentials> 형식이다
    //여기서 bearer는 <type>에 해당하며, 뜻은 'JWT 혹은 OAuth에 대한 토큰을 사용한다(RFC 6750)'이다(RFC 6750가 뭔가 했더니 표준명세서라네요..)
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);

        //실제 토큰 값을 추출?하기 위한 접두사 제거 후 return
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
