package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter
public class RspTemplate<T> {
    int statusCode; // HTTP 응답 코드
    String message; // 에러 메시지
    T data;         // 데이터(제너릭)

    // 오버로딩 1(응답코드, 에러 메시지, 데이터)
    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    // 오버로딩 2(응답코드, 에러 메시지)
    public RspTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}
