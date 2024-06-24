package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse(
        int statusCode,     // HTTP 상태 코드를 저장할 필드 statusCode 선언
        String message      // 에러 메시지를 저장할 필드 message 선언
) {
}