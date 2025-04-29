package com.cocos.cocos.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewImageDeleteListResponse(
        @Schema(description = "리뷰 삭제 presigned url", example = "[https://~, https://~]")
        List<String> images
) {
    public static ReviewImageDeleteListResponse of(final List<String> images) {
        return new ReviewImageDeleteListResponse(images);
    }
}
