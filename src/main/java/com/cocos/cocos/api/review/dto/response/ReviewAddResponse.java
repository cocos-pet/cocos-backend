package com.cocos.cocos.api.review.dto.response;

import java.util.List;

public record ReviewAddResponse(
        List<String> images
) {
    public static ReviewAddResponse of(final List<String> images) {
        return new ReviewAddResponse(images);
    }
}
