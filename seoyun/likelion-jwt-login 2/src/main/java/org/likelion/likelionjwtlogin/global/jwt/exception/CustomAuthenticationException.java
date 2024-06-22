package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;

//AuthenticationException 클래스를 상속
public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    } //생성자 내에서 super(message)를 호출하여 부모 클래스인 AuthenticationException의 생성자를 호출
}
