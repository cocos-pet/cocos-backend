package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReissueTokenResponse(
        @Schema(description = "토큰")
        TokenResponse tokens
) {
    public static ReissueTokenResponse of(final TokenResponse tokens) {
        return new ReissueTokenResponse(tokens);
    }
}
