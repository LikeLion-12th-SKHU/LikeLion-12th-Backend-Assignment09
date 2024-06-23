package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 응답 템플릿 클래스.
 *
 * @param <T> 응답 데이터의 타입을 나타냅니다.
 */
@Getter // Lombok 어노테이션으로, 모든 필드에 대한 getter 메서드를 자동으로 생성합니다.
public class RspTemplate<T> {
    int statusCode; // HTTP 상태 코드를 저장하는 필드
    String message; // 응답 메시지를 저장하는 필드
    T data; // 응답 데이터를 저장하는 제네릭 타입 필드

    /**
     * 응답 템플릿의 모든 필드를 초기화하는 생성자.
     *
     * @param httpStatus HTTP 상태 코드
     * @param message 응답 메시지
     * @param data 응답 데이터
     */
    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value(); // HTTP 상태 코드의 정수 값을 설정
        this.message = message; // 응답 메시지를 설정
        this.data = data; // 응답 데이터를 설정
    }

    /**
     * 응답 템플릿의 상태 코드와 메시지만 초기화하는 생성자.
     *
     * @param httpStatus HTTP 상태 코드
     * @param message 응답 메시지
     */
    public RspTemplate(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value(); // HTTP 상태 코드의 정수 값을 설정
        this.message = message; // 응답 메시지를 설정
    }
}
