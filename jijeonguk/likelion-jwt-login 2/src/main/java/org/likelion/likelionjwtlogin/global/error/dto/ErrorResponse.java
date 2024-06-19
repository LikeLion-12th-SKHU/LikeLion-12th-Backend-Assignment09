package org.likelion.likelionjwtlogin.global.error.dto;

// 에러 응답을 나타내는 레코드 클래스
public record ErrorResponse(
        int statusCode, // HTTP 상태 코드
        String message // 에러 메세지
) {
}