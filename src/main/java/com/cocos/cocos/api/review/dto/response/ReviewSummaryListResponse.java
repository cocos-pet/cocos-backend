package com.cocos.cocos.api.review.dto.response;

import java.util.List;

public record ReviewSummaryListResponse(
        List<ReviewSummaryResponse> goodReviews,
        List<ReviewSummaryResponse> badReviews
) {
    public static ReviewSummaryListResponse of(final List<ReviewSummaryResponse> goodReviews, final List<ReviewSummaryResponse> badReviews) {
        return new ReviewSummaryListResponse(goodReviews, badReviews);
    }
}
