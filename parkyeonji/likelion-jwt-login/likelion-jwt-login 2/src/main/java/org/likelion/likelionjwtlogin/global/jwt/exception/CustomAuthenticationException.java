package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;

// 사용자 인증 예외를 정의하는 클래스
public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    } //예외 메세지 설정
}
