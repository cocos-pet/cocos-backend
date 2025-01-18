package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "토큰")
        TokenResponse token,
        @Schema(description = "기존 멤버 여부", example = "false")
        boolean isCompletedSignUp
) {
    public static LoginResponse of(final TokenResponse token, final boolean isCompletedSignUp) {
        return new LoginResponse(token, isCompletedSignUp);
    }
}
