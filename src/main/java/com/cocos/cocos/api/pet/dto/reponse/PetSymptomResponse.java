package com.cocos.cocos.api.pet.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record PetSymptomResponse(
        @Schema(description = "증상 아이디", example = "1")
        Long id,
        @Schema(description = "증상 이름", example = "증상1")
        String name
) {
    public static PetSymptomResponse of(final Long id, final String name) {
        return new PetSymptomResponse(id, name);
    }
}
