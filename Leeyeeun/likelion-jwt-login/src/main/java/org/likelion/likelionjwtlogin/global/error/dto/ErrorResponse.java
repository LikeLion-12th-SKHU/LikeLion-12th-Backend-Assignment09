package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse(
        int statusCode, // HTTP 상태 코드를 나타냄
        String message // 오류 메세지 나타냄
) {
}