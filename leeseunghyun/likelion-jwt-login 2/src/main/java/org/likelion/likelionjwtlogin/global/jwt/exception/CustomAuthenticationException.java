package org.likelion.likelionjwtlogin.global.jwt.exception;

import org.springframework.security.core.AuthenticationException; //spring security 인증예외 클래스 import

public class CustomAuthenticationException extends AuthenticationException { //CustomAuthenticationException 클래스 정의하고 AuthenticationException을 상속
    public CustomAuthenticationException(String message) { //생성자 정의 예외 메세지를 받음
        super(message); //부모 클래스의 생성자를 호출하여 메세지를 전달함
    }
}
