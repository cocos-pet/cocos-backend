package com.cocos.cocos.api.breed.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BreedsResponse(
        @Schema(description = "품종 리스트")
        List<BreedResponse> breeds
) {
    public static BreedsResponse of(final List<BreedResponse> breeds) {
        return new BreedsResponse(breeds);
    }
}
