package com.cocos.cocos.api.member.dto.response;

public record MemberProfileResponse(
        String nickname,
        String profileImage
) {
    public static MemberProfileResponse of(final String nickname, final String profileImage) {
        return new MemberProfileResponse(nickname, profileImage);
    }
}
