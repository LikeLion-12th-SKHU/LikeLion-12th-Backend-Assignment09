package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse(
        int statusCode,         // HTTP 응답코드
        String message          // 응답 메시지
) {
}