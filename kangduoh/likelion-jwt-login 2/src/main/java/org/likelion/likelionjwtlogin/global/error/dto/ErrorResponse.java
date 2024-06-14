package org.likelion.likelionjwtlogin.global.error.dto;

// 에러 응답을 나타내는 레코드 클래스
public record ErrorResponse(
        // HTTP 상태 코드 필드
        int statusCode,
        // 에러 메시지 필드
        String message
) {
}