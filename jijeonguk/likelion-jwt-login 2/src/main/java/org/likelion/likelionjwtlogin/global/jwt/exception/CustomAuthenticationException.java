package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;
// 이 클래스는 Spring Security의 AuthenticationException를 상속 받아 상용자 인증 예외를 정의한다.
public class CustomAuthenticationException extends AuthenticationException {
    // 예외 메세지를 매개 변수로 받아 부모 클래스의 생성자를 호출한다.
    public CustomAuthenticationException(String message) {
        super(message);
    }
}
