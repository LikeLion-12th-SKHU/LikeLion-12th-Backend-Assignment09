package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
// 클래스의 모든 필드에 대해 getter 메서드 자동으로 생성
@Getter
public class RspTemplate<T> {
    // HTTP 상태 코드 필드
    int statusCode;
    // 응답 메시지 필드
    String message;
    // 응답 데이터 필드. 제네릭 타입 T로 지정하여 다양한 데이터 타입을 받을 수 있도록 한다.
    T data;

    // HTTP 상태 코드, 메시지, 데이터를 입력받아 객체를 초기화하는 생성자
    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value(); // HTTP 상태 코드의 정수 값으로 statusCode 초기화
        this.message = message; // 전달된 메시지로 message 필드 초기화
        this.data = data; // 전달된 데이터로 data 필드 초기화
    }

    // HTTP 상태 코드, 메시지를 입력받아 객체를 초기화하는 생성자
    public RspTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value(); // HTTP 상태 코드의 정수 값으로 statusCode 초기화
        this.message = message; // 전달된 메시지로 message 필드 초기화
    }
}
