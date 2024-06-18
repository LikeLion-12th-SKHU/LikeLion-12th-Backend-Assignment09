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

// Spring Bean 에 등록한다.
@Component 
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    // private final 형태의 ObjectMapper 를 객체로 생성
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override   // 매소드 재정의
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        // HTTP 응답코드(401)와 메시지를 호출
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // HTTP 응답코드(401)와 메시지를 담을 HashMap 을 data 로 하여 객체로 생성
        Map<String, Object> data = new HashMap<>();
        // data Map 에 각각 할당
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        data.put("message", authenticationException.getMessage());

        // 클라이언트에 응답을 보내는데 이진수 데이터를 out 에 저장
        OutputStream out = response.getOutputStream();
        // 출력
        objectMapper.writeValue(out, data);

        // out 에 있는 내용을 파일에 기록하고 버퍼를 비운다.
        out.flush();
    }
}
