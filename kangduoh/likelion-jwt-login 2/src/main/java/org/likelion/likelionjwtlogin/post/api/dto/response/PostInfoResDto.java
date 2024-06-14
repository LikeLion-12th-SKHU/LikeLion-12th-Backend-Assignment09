package org.likelion.likelionjwtlogin.post.api.dto.response;

import lombok.Builder;
import org.likelion.likelionjwtlogin.post.domain.Post;

@Builder
public record PostInfoResDto(
        String title,
        String content,
        String writer
) {
    public static PostInfoResDto from(Post post) {
        return PostInfoResDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getMember().getNickname())
                .build();
    }
}
