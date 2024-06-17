package org.likelion.likelionjwtlogin.global.template;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 응답 템플릿
@Getter //Lombok 의 Getter 애노테이션을 사용하여 모든 필드의 getter 메서드를 자동으로 생성
public class RspTemplate<T> { //제네릭: 클래스, 메소드에서 사용할 데이터 타입을 나중에 확정하는 기법 , 제네릭 클래스 정의
    int statusCode; //http 코드상태를 저장할 변수
    String message; //응답메세지를 저장할 변수
    T data; //응답 데이터를 저장할 변수

    public RspTemplate(HttpStatus httpStatus, String message, T data) {
        this.statusCode = httpStatus.value(); //http 상태 코드의 값 설정
        this.message = message; //메시지 설정
        this.data = data; //데이터 설정
    }

    public RspTemplate(HttpStatus httpStatus, String message) { //데이터 없이 상태 코드와 메시지만 초기화하는 생성자
        this.statusCode = httpStatus.value(); //http 상태 코드의 값 설정
        this.message = message; //메시지 설정
    }
}
