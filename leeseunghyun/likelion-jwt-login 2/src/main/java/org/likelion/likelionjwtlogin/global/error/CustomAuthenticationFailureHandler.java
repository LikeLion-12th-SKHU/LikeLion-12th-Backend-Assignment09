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

@Component //component 어노테이션은 빈 등록 자체를 빈 클래스에다가 할수 있다
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint { //인증 실패 처리 핸들러 클래스를 정의함
    private final ObjectMapper objectMapper = new ObjectMapper(); //ObjectMapper:객체로부터 json 형태의 문자을 만들어 , objector 객체 생성

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException { //인증 실패시 호출되는 메서드
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //http 상태 코드를 401로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); //응답 콘텐츠 타입을 json 으로 설정

        Map<String, Object> data = new HashMap<>(); //hashmap:많은 양의 데이터를 저장하는데 좋음, 응답 데이터를 저장할 Map 을 생성
        data.put("statusCode", HttpServletResponse.SC_UNAUTHORIZED); //상태 코드 추가
        data.put("message", authenticationException.getMessage()); //예외 메세지 추가

        OutputStream out = response.getOutputStream(); //응답 출력 스트림 가져오기
        objectMapper.writeValue(out, data); //출력 스트림 json 데이터를 작성
        out.flush(); //출력 스트림을 비움
    }
}
