package com.cocos.cocos.api.post.dto.response;

import java.util.List;

public record PopularPostsResponse(
        List<PopularPostResponse> posts
) {
    public static PopularPostsResponse of(final List<PopularPostResponse> posts) {
        return new PopularPostsResponse(posts);
    }
}
