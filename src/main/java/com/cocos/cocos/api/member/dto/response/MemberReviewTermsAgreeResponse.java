package com.cocos.cocos.api.member.dto.response;

public record MemberReviewTermsAgreeResponse(
        boolean isReviewTermsAgree
) {
    public static MemberReviewTermsAgreeResponse of(final boolean reviewTermsAgree) {
        return new MemberReviewTermsAgreeResponse(reviewTermsAgree);
    }
}
