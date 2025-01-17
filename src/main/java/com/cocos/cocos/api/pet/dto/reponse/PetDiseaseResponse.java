package com.cocos.cocos.api.pet.dto.reponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record PetDiseaseResponse(
        @Schema(description = "질병 아이디", example = "1")
        Long id,
        @Schema(description = "질병 이름",example = "질병1")
        String name
) {
    public static PetDiseaseResponse of(final Long id, final String name) {
        return new PetDiseaseResponse(id, name);
    }
}
