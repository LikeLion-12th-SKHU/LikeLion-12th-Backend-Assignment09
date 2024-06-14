package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;

// AuthenticationException 클래스를 상속받아 사용자 인증 예외 클래스를 정의
public class CustomAuthenticationException extends AuthenticationException {
    // 생성자. 인증 예외 메시지를 전달받아 상위 클래스의 생성자를 호출하여 초기화
    public CustomAuthenticationException(String message) {
        super(message);
    }
}
