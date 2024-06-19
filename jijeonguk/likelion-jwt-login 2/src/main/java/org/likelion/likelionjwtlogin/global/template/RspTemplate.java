package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter
public class RspTemplate<T> {
    // HTTP 상태 코드 필드
    int statusCode;
    // 응답 메세지 필드
    String message;
    // 제네릭 타입 데이터 필드
    T data;

    // HTTP 상태코드와, 응답 메세지 필드, 데이터 필드를 초기화하는 생성자
    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    // HTTP 상태코드와 응답 메세지 필드만 초기화하는 생성자
    public RspTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}
