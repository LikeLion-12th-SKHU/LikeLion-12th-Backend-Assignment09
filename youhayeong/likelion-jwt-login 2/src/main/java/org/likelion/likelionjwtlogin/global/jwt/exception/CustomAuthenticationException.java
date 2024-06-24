package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException;

// TokenProvider 에서 토큰 검증에 실패한 경우 customexception을 통해 메시지 출력으로 처리
public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    }
}
