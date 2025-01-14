package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostCategoryResponse(
        @Schema(description = "게시글 카테고리 아이디", example = "1")
        Long id,
        @Schema(description = "게시글 카테고리 이름", example = "증상/질병 고민")
        String name,
        @Schema(description = "게시글 카테고리 이미지", example = "https://~~")
        String image
) {
    public static PostCategoryResponse of(final Long id, final String name, final String image) {
        return new PostCategoryResponse(id, name, image);
    }
}
