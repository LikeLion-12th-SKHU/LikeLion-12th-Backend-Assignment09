package org.likelion.likelionjwtlogin.member.api.dto.response;

import lombok.Builder;
import org.likelion.likelionjwtlogin.member.api.dto.request.MemberLoginReqDto;
import org.likelion.likelionjwtlogin.member.domain.Member;

@Builder
public record MemberLoginResDto(
        String email,
        String nickname,
        String token
) {
    public static MemberLoginResDto of(Member member, String token) {
        return MemberLoginResDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .token(token)
                .build();
    }
}
