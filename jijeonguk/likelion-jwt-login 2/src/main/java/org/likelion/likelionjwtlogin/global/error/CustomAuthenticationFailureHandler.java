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

// Spring Bean에 등록한다.
@Component
// 이 클래스는 인증 실패 처리 핸들러이다.
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Json 변환을 위해 ObjectMapper 객체를 생성한다.
    @Override
    // 인증이 실패 할 때 호출되는 메서드
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        // 응답의 상태 코드를 401로 설정한다.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 응답의 Content Type을 Json 형싣으로 설정한다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 응답 데이터로 사용할 Map을 생성한다.
        Map<String, Object> data = new HashMap<>();
        // 상태 코드를 Map에 추가
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        // 예외 메세지를 Map에 추가
        data.put("message", authenticationException.getMessage());

        // 응답 출력 스트림을 가져온다.
        OutputStream out = response.getOutputStream();
        // Map 객체를 Json 형식으로 변환하여 출력 스트임에 작성한다.
        objectMapper.writeValue(out, data);
        // 출력 스트림을 비운다.
        out.flush();
    }
}
