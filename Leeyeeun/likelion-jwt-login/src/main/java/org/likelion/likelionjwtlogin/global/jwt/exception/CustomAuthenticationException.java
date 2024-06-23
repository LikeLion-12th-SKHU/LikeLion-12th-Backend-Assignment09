package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    }
} // 상위 클래스의 생성자를 호출 후 메세지 전달
