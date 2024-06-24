package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter
public class RspTemplate<T> {
    int statusCode;     // 응답 상태 코드 저장할 필드 선언
    String message;     // 응답 메시지 저장할 필드 선언
    T data;     // 응답 데이터를 저장할 제네릭 타입 필드 선언

    // 상태 코드, 메시지, 데이터를 입력 받을 경우
    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value();
        this.message = message;
        this.data = data;
    }

    // 상태 코드, 메시지를 입력 받을 경우
    public RspTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}
