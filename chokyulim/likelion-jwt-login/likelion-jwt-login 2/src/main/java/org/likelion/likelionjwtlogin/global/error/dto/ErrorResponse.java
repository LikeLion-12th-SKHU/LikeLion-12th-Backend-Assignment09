package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse(
        int statusCode, // http 상태 코드
        String message // 오류 메시지
) {
}