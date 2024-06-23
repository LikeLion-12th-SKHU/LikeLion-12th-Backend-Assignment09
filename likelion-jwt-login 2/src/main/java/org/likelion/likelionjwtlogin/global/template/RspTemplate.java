package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter
public class RspTemplate<T> {
    int statusCode; //응답코드
    String message; //응답 메시지
    T data; //응답 데이터

    public RspTemplate(HttpStatus httpStatus, String message, T data) { //응답데이터 풀 생성자.
        this.statusCode = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    public RspTemplate(HttpStatus httpStatus, String message) { //데이터 없이 메시지보낼 경우 생성자
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}
