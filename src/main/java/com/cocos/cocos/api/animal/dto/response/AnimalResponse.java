package com.cocos.cocos.api.animal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AnimalResponse(
        @Schema(description = "동물 아이디", example = "1")
        Long id,
        @Schema(description = "동물 이름", example = "강아지")
        String name,
        @Schema(description = "동물 이미지", example = "http://~~")
        String image
) {
    public static AnimalResponse of(final Long id, final String name, final String image) {
        return new AnimalResponse(id, name, image);
    }
}
