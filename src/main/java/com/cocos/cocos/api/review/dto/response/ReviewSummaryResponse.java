package com.cocos.cocos.api.review.dto.response;

public record ReviewSummaryResponse(
        Long id,
        String label,
        int count
) {
    public static ReviewSummaryResponse of(final Long id, final String label, final int count) {
        return new ReviewSummaryResponse(id, label, count);
    }
}
