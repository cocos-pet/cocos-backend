package com.cocos.cocos.api.location.dto.response;

public record DistrictResponse(
        Long id,
        String name
) {
    public static DistrictResponse of(final Long id, final String name) {
        return new DistrictResponse(id, name);
    }
}
