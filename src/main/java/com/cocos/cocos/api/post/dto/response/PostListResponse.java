package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostListResponse(
        @Schema(description = "마지막 게시글 아이디", example = "1 or null")
        Long cursorId,
        @Schema(description = "게시글 리스트")
        List<PostResponse> posts
) {
    public static PostListResponse of(final Long cursorId, final List<PostResponse> posts) {
        return new PostListResponse(cursorId, posts);
    }
}
