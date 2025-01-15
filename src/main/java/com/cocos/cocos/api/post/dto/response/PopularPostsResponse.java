package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PopularPostsResponse(
        @Schema(description = "인기 게시글 리스트")
        List<PopularPostResponse> posts
) {
    public static PopularPostsResponse of(final List<PopularPostResponse> posts) {
        return new PopularPostsResponse(posts);
    }
}
