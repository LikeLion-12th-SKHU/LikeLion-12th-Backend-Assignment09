package org.likelion.likelionjwtlogin.global.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component // 스프링 빈으로 등록
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint { // 인증 실패를 처리하는 클래스 선언
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper 객체를 생성

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException { // 인증 실패 시 호출되는 메서드
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 응답 상태 401 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 응답 타입 JSON 설정

        Map<String, Object> data = new HashMap<>(); // 응답 데이터 담을 맵 생성
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED); // 상태 코드 맵에 추가
        data.put("message", authenticationException.getMessage()); // 에외 처리 메세지 맵에 추가

        OutputStream out = response.getOutputStream(); // 응답 출력 스트림 가져옴
        objectMapper.writeValue(out, data); // 맵 데이터를 JSON 형식으로 변환 후 출력 스트림에 씀
        out.flush(); // 출력 스트림 비움
    }
}
