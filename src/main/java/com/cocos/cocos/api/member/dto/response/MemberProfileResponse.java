package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberProfileResponse(
        @Schema(description = "닉네임", example = "모모")
        String nickname,
        @Schema(description = "프로필 이미지", example = "http://~~")
        String profileImage
) {
    public static MemberProfileResponse of(final String nickname, final String profileImage) {
        return new MemberProfileResponse(nickname, profileImage);
    }
}
