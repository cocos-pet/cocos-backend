package com.cocos.cocos.api.location.dto.response;

import java.util.List;

public record CityResponse(
        Long id,
        String name,
        List<DistrictResponse> districts
) {
    public static CityResponse of(final Long id, final String name, final List<DistrictResponse> districts) {
        return new CityResponse(id, name, districts);
    }
}
