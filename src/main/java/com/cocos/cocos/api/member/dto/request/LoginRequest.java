package com.cocos.cocos.api.member.dto.request;

import com.cocos.cocos.enums.member.Platform;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "소셜 플랫폼", example = "KAKAO")
        Platform platform,
        @Schema(description = "코드", example = "egagasdgasdgdagdasgasgasdgdasgasdglkj")
        String code
) {
}
