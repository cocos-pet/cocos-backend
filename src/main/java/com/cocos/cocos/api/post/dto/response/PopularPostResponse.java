package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PopularPostResponse(
        @Schema(description = "게시글 아이디", example = "1")
        Long id,
        @Schema(description = "게시글 제목", example = "인기 게시글")
        String title
) {
    public static PopularPostResponse of(final Long id, final String title) {
        return new PopularPostResponse(id, title);
    }
}
