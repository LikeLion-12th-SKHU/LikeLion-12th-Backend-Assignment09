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

@Component  // spring Bean에 등록
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    // Json 처리를 위한 클래스
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override   // 인증 실패시 처리
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 인증 받지 않은 사용자
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // json 타입으로 설정

        Map<String, Object> data = new HashMap<>(); // 응답 데이터 담을 HashMap 생성
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);    // 상태 코드 추가
        data.put("message", authenticationException.getMessage());      // 예외 메시지 추가

        OutputStream out = response.getOutputStream();  // 응답 객체의 출력 스트림에
        objectMapper.writeValue(out, data);             // write
        out.flush();
    }
}
