package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MemberPostsResponse(
        @Schema(description = "사용자 게시글 리스트")
        List<MemberPostDetailResponse> posts
) {
    public static MemberPostsResponse of(final List<MemberPostDetailResponse> posts) {
        return new MemberPostsResponse(posts);
    }
}
