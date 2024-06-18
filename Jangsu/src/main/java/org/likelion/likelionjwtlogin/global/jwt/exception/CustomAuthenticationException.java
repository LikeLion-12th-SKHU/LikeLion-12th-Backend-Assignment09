package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;
// AuthenticationException(부모) 를 CustomAuthenticationException(자식) 으로 상속한다.
public class CustomAuthenticationException extends AuthenticationException {
    // 메시지를 매개변수로 받는다.
    public CustomAuthenticationException(String message) {
        // 부모 클래스의 생성자를 호출한다.
        super(message);
    }
}
