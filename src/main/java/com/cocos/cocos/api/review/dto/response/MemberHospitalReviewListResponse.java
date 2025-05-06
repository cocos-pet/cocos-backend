package com.cocos.cocos.api.review.dto.response;

import java.util.List;

public record MemberHospitalReviewListResponse(
        Long cursorId,
        List<MemberHospitalReviewResponse> reviews
) {
    public static MemberHospitalReviewListResponse of(final Long cursorId, final List<MemberHospitalReviewResponse> reviewResponses) {
        return new MemberHospitalReviewListResponse(cursorId, reviewResponses);
    }
}
