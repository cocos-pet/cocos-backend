package com.cocos.cocos.api.post.dto.response;

public record PopularPostResponse(
        Long id,
        String title
) {
    public static PopularPostResponse of(final Long id, final String title) {
        return new PopularPostResponse(id, title);
    }
}
