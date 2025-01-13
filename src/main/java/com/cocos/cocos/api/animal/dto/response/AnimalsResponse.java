package com.cocos.cocos.api.animal.dto.response;

import java.util.List;

public record AnimalsResponse(
        List<AnimalResponse> animals
) {
    public static AnimalsResponse of(final List<AnimalResponse> animals) {
        return new AnimalsResponse(animals);
    }
}
