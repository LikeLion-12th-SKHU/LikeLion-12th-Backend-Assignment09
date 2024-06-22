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
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 생성

    //HttpServletRequest를 통해 요청한 정보를 받음
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //HttpServletResponse: 요청에 대한 응답을 생성
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //MediaType타입 지정

        Map<String, Object> data = new HashMap<>();
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED); //HttpServletResponse.SC_UNAUTHORIZED: 클라이언트가 인증되지 않았음을 나타내는 상수
        data.put("message", authenticationException.getMessage()); //예외에 대한 설명

        OutputStream out = response.getOutputStream(); // 회원에게 데이터 보내는 데 사용함
        objectMapper.writeValue(out, data); //Java 객체를 JSON 문자열로 변환하는 데 사용함
        out.flush(); //버퍼에 남아있는 모든 데이터 출력
    }
}
