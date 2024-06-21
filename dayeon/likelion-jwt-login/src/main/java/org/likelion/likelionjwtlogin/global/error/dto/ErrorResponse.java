package org.likelion.likelionjwtlogin.global.error.dto;

public record ErrorResponse(
        int statusCode,
        String message
) {
}