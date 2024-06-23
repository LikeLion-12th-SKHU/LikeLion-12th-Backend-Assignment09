package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter
public class RspTemplate<T> {
    int statusCode; // HTTP 상태 코드를 저장
    String message; // 응답 메세지를 저장
    T data; // 제네틱 타입의 데이터를 저장

    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value(); // HTTP 상태 코드 값을 statusCode 필드에 저장
        this.message = message; // 메세지를 message 필드에 저장
        this.data = data; // 데이터를 data 필드에 저장
    }

    public RspTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value(); // HTTP 상태 코드 값을 statusCode 필드에 저장
        this.message = message; // 메세지를 message 필드에 저장
    }
}
