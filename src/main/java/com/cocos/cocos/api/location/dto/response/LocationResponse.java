package com.cocos.cocos.api.location.dto.response;

import java.util.List;

public record LocationResponse(
        List<CityResponse> cities
) {
    public static LocationResponse of(final List<CityResponse> cities) {
        return new LocationResponse(cities);
    }
}
