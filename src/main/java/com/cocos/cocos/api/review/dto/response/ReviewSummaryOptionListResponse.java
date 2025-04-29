package com.cocos.cocos.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewSummaryOptionListResponse(
        @Schema(description = "좋은 리뷰 요약 리스트")
        List<ReviewSummaryOptionResponse> goodReviews,
        @Schema(description = "나쁜 리뷰 요약 리스트")
        List<ReviewSummaryOptionResponse> badReviews
) {
    public static ReviewSummaryOptionListResponse of(final List<ReviewSummaryOptionResponse> goodReviews, final List<ReviewSummaryOptionResponse> badReviews) {
        return new ReviewSummaryOptionListResponse(goodReviews, badReviews);
    }
}
