package com.cocos.cocos.api.member.dto.request;

import com.cocos.cocos.enums.member.Platform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @Schema(description = "소셜 플랫폼", example = "KAKAO")
        @NotNull(message = "소셜 플랫폼은 필수입니다.")
        Platform platform,
        @Schema(description = "코드", example = "egagasdgasdgdagdasgasgasdgdasgasdglkj")
        @NotBlank(message = "코드는 필수입니다. ")
        String code,
        @Schema(description = "리다이렉트 uri", example = "http://localhost~/auth")
        @NotBlank(message = "리다이렉트 uri 는 필수입니다. ")
        String redirectUri
) {
}
