package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse(
        int statusCode, // HTTP 상태 코드
        String message // 에러 메시지
) {
}