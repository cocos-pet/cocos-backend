package com.cocos.cocos.api.location.dto.response;

public record DistrictResponse(
        Long id,
        String name,
        String type
) {
    public static DistrictResponse of(final Long id, final String name, final String type) {
        return new DistrictResponse(id, name, type);
    }
}
