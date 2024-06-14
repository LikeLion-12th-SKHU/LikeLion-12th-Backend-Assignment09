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
// AuthenticationEntryPoint 인터페이스를 구현하여 인증 실패 시 사용자에게 에러 메시지를 반환한다.
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    // HTTP 응답을 JSON 형식으로 변환하기 위해 사용하는 ObjectMapper 객체
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 인증 실패 시 호출되는 메서드
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        // HTTP 응답 상태 코드를 401 Unauthorized 로 설정
        // 401 Unauthorized 는 클라이언트가 인증되지 않았거나, 유효한 인증 정보가 부족하여 요청이 거부됨을 의미
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // HTTP 응답의 콘텐츠 타입을 JSON 으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 응답으로 보낼 데이터를 담을 Map 객체 생성
        Map<String, Object> data = new HashMap<>();
        // HTTP 상태 코드와 에러 메시지를 Map 에 추가
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED);
        data.put("message", authenticationException.getMessage());

        // 응답 출력 스트림을 얻어온다.
        OutputStream out = response.getOutputStream();
        // Map 객체를 JSON 형식으로 변환하여 응답 출력 스트림에 쓴다.
        objectMapper.writeValue(out, data);
        // 출력 스트림을 비운다.
        out.flush();
    }
}
