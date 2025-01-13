package com.cocos.cocos.api.animal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AnimalsResponse(
        @Schema(description = "동물 리스트")
        List<AnimalResponse> animals
) {
    public static AnimalsResponse of(final List<AnimalResponse> animals) {
        return new AnimalsResponse(animals);
    }
}
