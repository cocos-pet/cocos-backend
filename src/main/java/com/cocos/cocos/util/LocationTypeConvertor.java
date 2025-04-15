package com.cocos.cocos.util;

import com.cocos.cocos.enums.location.LocationType;
import org.springframework.core.convert.converter.Converter;

public class LocationTypeConvertor implements Converter<String, LocationType> {

    @Override
    public LocationType convert(final String locationType) {
        return LocationType.create(locationType.toUpperCase());
    }


}
