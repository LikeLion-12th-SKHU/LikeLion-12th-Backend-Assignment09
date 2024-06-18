package org.likelion.likelionjwtlogin.member.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginReqDto(
        @NotBlank
        String email,

        @NotBlank
        String pwd,

        @NotBlank
        String nickname
) {
}
/*
    DTO 는 왜 record 로 만들었는가?

    1. 불변객체이므로 값이 변하지 않는다.
    2. 데이터 객체를 쉽게 만들 수 있다.
    3. 모든 필드는 자동으로 private final 로 선언된다.
    4. Getter, toString, 생성자 등을 자동을 만들어 준다.
*/
// @NotBlank : null, 공백 모두 허용하지 않는다.