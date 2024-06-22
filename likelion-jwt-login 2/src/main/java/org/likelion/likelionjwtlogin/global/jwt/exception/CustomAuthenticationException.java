package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException { //인증 예외를 확장한 사용자정의 인증 예외 클래스
    public CustomAuthenticationException(String message) {
        super(message);
    } //사용자 인증 예외
}
