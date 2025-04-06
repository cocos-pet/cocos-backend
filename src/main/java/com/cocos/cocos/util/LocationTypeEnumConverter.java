package com.cocos.cocos.util;

import com.cocos.cocos.enums.location.LocationType;
import org.springframework.core.convert.converter.Converter;

public class LocationTypeEnumConverter implements Converter<String, LocationType> {

    @Override
    public LocationType convert(String requestCategory) {
        return LocationType.create(requestCategory.toUpperCase());
    }
}
