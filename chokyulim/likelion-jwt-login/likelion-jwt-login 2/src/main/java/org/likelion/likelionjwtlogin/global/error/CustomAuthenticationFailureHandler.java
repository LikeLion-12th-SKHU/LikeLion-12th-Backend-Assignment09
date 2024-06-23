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

@Component // 이 클래스를 스프링 빈으로 등록하여 스프링 컨텍스트에서 관리할 수 있게 함
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint { // 인증 실패를 처리하는 클래스 선언, AuthenticationEntryPoint 인터페이스 구현

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 Jackson의 ObjectMapper 객체를 생성

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException { // 인증 실패 시 호출되는 메서드

        // 응답 상태 코드를 401 (Unauthorized)로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 응답의 Content-Type을 JSON으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 응답에 담을 데이터를 저장할 맵 객체 생성
        Map<String, Object> data = new HashMap<>();
        // 상태 코드와 예외 메시지를 맵에 추가
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED); // 상태 코드를 401로 추가
        data.put("message", authenticationException.getMessage()); // 예외 메시지를 추가

        // 응답 출력 스트림을 가져옴
        OutputStream out = response.getOutputStream();
        // 맵 데이터를 JSON 형식으로 변환하여 출력 스트림에 씀
        objectMapper.writeValue(out, data);
        // 출력 스트림을 비워줌 (flush)
        out.flush();
    }
}
