package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostImagesResponse(
        @Schema(description = "게시글 이미지 presigned url", example = "[1, 2]")
        List<String> images
) {
    public static PostImagesResponse of(final List<String> images) {
        return new PostImagesResponse(images);
    }
}
