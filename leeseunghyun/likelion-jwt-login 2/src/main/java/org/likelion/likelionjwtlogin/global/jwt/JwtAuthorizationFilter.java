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

@Component //component 어노테이션은 빈 등록 자체를 빈 클래스에다가 할수 있다
@RequiredArgsConstructor //생성자 주입
public class JwtAuthorizationFilter extends OncePerRequestFilter { //JWT 인증 필터 클래스 정의하고 OncePerRequestFilter를 상속함
    private static final String AUTHORIZATION_HEADER = "Authorization"; //Authorization 헤더의 이름을 정의함
    private static final String BEARER_PREFIX = "Bearer "; //Bearer 토큰 정의

    private final TokenProvider tokenProvider; //토큰 제공자 클래스
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler; //커스텀 인증 실패 핸들러

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException { //필터링 로직 구현 메서드이다

        try {
            String token = resolveToken(request); //요청을 통해 토큰을 추출한다

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) { //토큰이 존재하고 유해한경우
                Authentication authentication = tokenProvider.getAuthentication(token); //토큰에서 인증 객체를 생성함
                SecurityContextHolder.getContext().setAuthentication(authentication); //보안 컨텍스트에 인증 객체를설정
            }

            filterChain.doFilter(request, response); //다음 필터로 요청과 응답을 전달
        } catch (CustomAuthenticationException e) { //커스텀 인증예외가 발생한 경우 포착
            customAuthenticationFailureHandler.commence(request, response, new CustomAuthenticationException(e.getMessage())); //인증을 실패했을때 처리하는 과정
        }

    }

    private String resolveToken(HttpServletRequest request) { //요청에서 토큰 추출하는 메서드
        String token = request.getHeader(AUTHORIZATION_HEADER); //Authorization 헤더에서 토큰을 가져옴

        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) { //토큰이 존재하고 이름이 Bearer 인 경우
            return token.substring(BEARER_PREFIX.length()); // 이름을 제거하 토큰반환
        }

        return null; //토큰이 존재하지않으면 널값을 채워넣기
    }
}
