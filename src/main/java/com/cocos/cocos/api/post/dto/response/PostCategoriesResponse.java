package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostCategoriesResponse(
        @Schema(description = "게시글 카테고리 리스트")
        List<PostCategoryResponse> categories
) {
    public static PostCategoriesResponse of(final List<PostCategoryResponse> categories) {
        return new PostCategoriesResponse(categories);
    }
}
