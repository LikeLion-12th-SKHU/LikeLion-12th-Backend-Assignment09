package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse(
        int statusCode, // 에러 status 코드
        String message // 에러 메시지
) {
}