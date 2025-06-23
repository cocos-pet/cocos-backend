package com.cocos.cocos.api.location.dto.response;

public record DistrictResponse(
        Long id,
        String name,
        String locationType
) {
    public static DistrictResponse of(final Long id, final String name, final String locationType) {
        return new DistrictResponse(id, name, locationType);
    }
}
