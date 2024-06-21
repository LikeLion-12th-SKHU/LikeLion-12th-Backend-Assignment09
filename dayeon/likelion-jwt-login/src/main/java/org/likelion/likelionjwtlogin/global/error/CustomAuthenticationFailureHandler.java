package org.likelion.likelionjwtlogin.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
//CustomAuthenticationFailureHandler(직독직해 그대로 사용자 인증 실패 핸들러) : 인증이 실패했을 때 사용
//AuthenticationEntryPoint : 인증 예외 발생 시 호출되는 AuthenticationException 커스텀이 가능한 인터페이스
//인가 예외 발생 시에는 AccessDeniedException 호출되어 AccessDeniedHandler 인터페이스를 사용해야 함
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    //AuthenticationEntryPoint의 commence()를 오버라이딩하여 인증 예외 로직 작성
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        //응답 상태 설정, UNAUTHORIZED : 401, '클라이언트가 인증되지 않아 요청을 정상적으로 처리할 수 없다'
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //응답 형태 설정, APPLICATION_JSON_VALUE : JSON 형식으로 출력
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        //발생한 예외를 JSON 형식으로 출력하기 위한 작업이다
        Map<String, Object> data = new HashMap<>();
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        data.put("message", authenticationException.getMessage());

        OutputStream out = response.getOutputStream();
        //objectMapper : Java 객체 <-> JSON을 수행하는 Jackson 라이브러리 클래스
        objectMapper.writeValue(out, data);
        out.flush();
    }
}
