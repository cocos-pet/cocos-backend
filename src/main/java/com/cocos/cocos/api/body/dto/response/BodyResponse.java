package com.cocos.cocos.api.body.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record BodyResponse(
        @Schema(description = "신체 부위 아이디", example = "1")
        Long id,
        @Schema(description = "신체 부위 이름", example = "손")
        String name,
        @Schema(description = "신체 부위 이미지", example = "http://~")
        String image
) {
    public static BodyResponse of(final Long id, final String name, final String image) {
        return new BodyResponse(id, name, image);
    }
}
