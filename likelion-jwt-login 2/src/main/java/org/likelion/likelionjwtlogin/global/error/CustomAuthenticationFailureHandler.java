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
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint { //AuthenticationEntryPoint 인터페이스를 구현하여 인증 실패시 에러메시지를 던지는 역할.
    private final ObjectMapper objectMapper = new ObjectMapper(); //json객체로 던지기 편하기위해 objectMapper 주입.

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 에러 status -> UnAuthorized로 설정.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //json 타입으로 response 응답의 contentType설정

        Map<String, Object> data = new HashMap<>();         //response data를 담을 hash map 생성
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED); //상태 코드
        data.put("message", authenticationException.getMessage()); //메시지를 담고

        OutputStream out = response.getOutputStream(); //output stream에
        objectMapper.writeValue(out, data); // write함.
        out.flush(); //output stream에 남아있는애들 비우기.
    }
}
