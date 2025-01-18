package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(
        @Schema(description = "어세스 토큰", example = "egwg.adgad.asdgas")
        String accessToken,
        @Schema(description = "리프레시 토큰", example = "egwg.adgad.asdgas")
        String refreshToken
) {
    public static TokenResponse of(final String accessToken, final String refreshToken) {
        return new TokenResponse(accessToken, refreshToken);
    }
}
