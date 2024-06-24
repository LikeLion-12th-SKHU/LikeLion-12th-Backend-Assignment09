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

@Component // 스프링 컴포넌트 스캔에 포함시켜 스프링 빈으로 등록
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSOM 처리를 위한 ObjectMapper 객체 생성

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 응답 상태를 401로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 응답 Content-type을 JSON으로 설정

        Map<String, Object> data = new HashMap<>(); // 응답 데이터를 담을 Map 객체 생성
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        data.put("message", authenticationException.getMessage());

        OutputStream out = response.getOutputStream(); // 응답 출력을 위한 스트림을 가져옴
        objectMapper.writeValue(out, data); // JSON 형식으로 변환
        out.flush(); // 출력 스트림 비우기
    }
}
