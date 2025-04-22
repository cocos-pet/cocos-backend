package com.cocos.cocos.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewAddResponse(
        @Schema(description = "리뷰 이미지 presigned url", example = "[https://~, https://~]")
        List<String> images
) {
    public static ReviewAddResponse of(final List<String> images) {
        return new ReviewAddResponse(images);
    }
}
