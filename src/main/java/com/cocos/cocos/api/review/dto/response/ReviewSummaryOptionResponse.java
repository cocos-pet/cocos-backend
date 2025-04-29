package com.cocos.cocos.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewSummaryOptionResponse(
        @Schema(description = "리뷰 요약 아이디", example = "1")
        Long id,
        @Schema(description = "리뷰 요약 내용", example = "~게 너무 좋았어요!")
        String label
) {
    public static ReviewSummaryOptionResponse of(final Long id, final String label) {
        return new ReviewSummaryOptionResponse(id, label);
    }
}
