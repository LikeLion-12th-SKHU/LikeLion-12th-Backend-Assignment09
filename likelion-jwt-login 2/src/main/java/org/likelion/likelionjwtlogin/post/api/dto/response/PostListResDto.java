package org.likelion.likelionjwtlogin.post.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PostListResDto(
        List<PostInfoResDto>  posts
) {
    public static PostListResDto from(List<PostInfoResDto> posts) {
        return PostListResDto.builder()
                .posts(posts)
                .build();
    }
}
