package org.likelion.likelionjwtlogin.global.error.dto;

//레코드 클래스
public record ErrorResponse(
        int statusCode, //정수 타입의 에러 상태코드
        String message //문자 타입의 메세지
) {
}