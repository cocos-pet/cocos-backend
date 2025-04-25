package com.cocos.cocos.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewSummaryListResponse(
        @Schema(description = "좋은 리뷰 요약 리스트")
        List<ReviewSummaryResponse> goodReviews,
        @Schema(description = "나쁜 리뷰 요약 리스트")
        List<ReviewSummaryResponse> badReviews
) {
    public static ReviewSummaryListResponse of(final List<ReviewSummaryResponse> goodReviews, final List<ReviewSummaryResponse> badReviews) {
        return new ReviewSummaryListResponse(goodReviews, badReviews);
    }
}
