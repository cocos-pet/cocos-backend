package com.cocos.cocos.api.animal.dto.response;

public record AnimalResponse(
        Long id,
        String name,
        String image
) {
    public static AnimalResponse of(final Long id, final String name, final String image) {
        return new AnimalResponse(id, name, image);
    }
}
