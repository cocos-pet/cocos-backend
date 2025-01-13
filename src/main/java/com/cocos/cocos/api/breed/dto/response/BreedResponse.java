package com.cocos.cocos.api.breed.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record BreedResponse(
        @Schema(description = "품종 아이디", example = "1")
        Long id,
        @Schema(description = "품종 이름", example = "포메라니안")
        String name
) {
    public static BreedResponse of(final Long id, final String name) {
        return new BreedResponse(id, name);
    }
}
