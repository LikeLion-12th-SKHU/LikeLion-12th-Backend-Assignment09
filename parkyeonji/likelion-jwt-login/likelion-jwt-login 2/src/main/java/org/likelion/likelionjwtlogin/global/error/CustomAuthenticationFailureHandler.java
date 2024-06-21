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
// 인증 실패 처리 핸들러
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    //AuthenticationEntryPoint: 인증 실패 시 처리를 위해 구현해야 하는 인터페이스
    private final ObjectMapper objectMapper = new ObjectMapper(); //JSON 형식으로 변환하기 위한 객체

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 상태코드로 설정. 인증 실패를 나타냄
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //응답의 컨텐츠 타입을 JSON으로 설정

        Map<String, Object> data = new HashMap<>(); //응답 데이터를 담기 위한 객체
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED); //상태 코드 추가
        data.put("message", authenticationException.getMessage()); //예외 메세지 추가

        OutputStream out = response.getOutputStream();
        objectMapper.writeValue(out, data); //data 객체를 JSON 문자열로 변환하고 클라이언트에 출력
        out.flush(); //데이터를 완전히 전송
    }
}
