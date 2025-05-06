package com.cocos.cocos.api.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record HospitalReviewListResponse(
        @Schema(description = "리뷰수", example = "111", nullable = true)
        Integer reviewCount,
        @Schema(description = "커서 아이디 (마지막으로 전달받은 리뷰 아이디)", example = "12")
        Long cursorId,
        List<HospitalReviewResponse> reviews
) {
    public static HospitalReviewListResponse of(final Integer reviewCount, final Long cursorId, final List<HospitalReviewResponse> reviews) {
        return new HospitalReviewListResponse(reviewCount, cursorId, reviews);
    }
}
