package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter
public class RspTemplate<T> {
    int statusCode; // HTTP 상태 코드
    String message; // 응답 메시지
    T data; // 응답 템플릿 생성자

    public RspTemplate(HttpStatus httpStatus, String message, T data) { // HTTP 상태 코드, 메시지, 데이터를 받아 초기화하는 생성자
        this.statusCode = httpStatus.value(); // HTTP 상태 코드의 정수값으로 초기화
        this.message = message; // 전달된 메시지로 초기화
        this.data = data; // 전달된 데이터로 초기화
    }

    public RspTemplate(HttpStatus httpStatus, String message) { //  HTTP 상태 코드와 메시지를 받아 초기화하는 생성자
        this.statusCode = httpStatus.value(); //
        this.message = message;
    }
}
