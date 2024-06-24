package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    } // 클래스 생성자 호출
}
