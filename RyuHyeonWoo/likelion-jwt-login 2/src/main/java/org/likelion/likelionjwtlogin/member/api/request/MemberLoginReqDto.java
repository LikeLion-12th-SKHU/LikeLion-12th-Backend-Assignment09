package org.likelion.likelionjwtlogin.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginReqDto(
        @NotBlank
        String email,

        @NotBlank
        String pwd
) {
}
