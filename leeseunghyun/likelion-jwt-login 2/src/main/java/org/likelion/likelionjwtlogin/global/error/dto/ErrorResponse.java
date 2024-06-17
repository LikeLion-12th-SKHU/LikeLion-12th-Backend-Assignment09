package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse( //에러 응답
        int statusCode, //Http 상태 응답
        String message //에러 메세지
) {
}