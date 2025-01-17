package com.cocos.cocos.api.post.dto.response;

import java.util.List;

public record PostListResponse(
        Long cursorId,
        List<PostResponse> posts
) {
    public static PostListResponse of(final Long cursorId, final List<PostResponse> posts) {
        return new PostListResponse(cursorId, posts);
    }
}
