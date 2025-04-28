package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberReviewTermsAgreeResponse(
        @Schema(description = "리뷰 약관 동의 여부", example = "true")
        boolean isReviewTermsAgree
) {
    public static MemberReviewTermsAgreeResponse of(final boolean reviewTermsAgree) {
        return new MemberReviewTermsAgreeResponse(reviewTermsAgree);
    }
}
