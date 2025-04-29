package com.cocos.cocos.api.review.dto.response;

import java.util.List;

public record ReviewImageDeleteListResponse(
        List<String> images
) {
    public static ReviewImageDeleteListResponse of(final List<String> images) {
        return new ReviewImageDeleteListResponse(images);
    }
}
