package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter
public class RspTemplate<T> {
    int statusCode; //HTTP 상태 코드
    String message; //응답 메세지
    T data;

    //HTTP 상태 코드, 응답 메세지, 데이터 필드 초기화
    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    //HTTP 상태 코드, 응답 메세지 필드만 초기화
    public RspTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}
