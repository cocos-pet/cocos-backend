package com.cocos.cocos.enums.location;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.enums.message.FailMessage;

public enum LocationType {
    CITY,
    DISTRICT,
    TOWN;

    public static LocationType create(final String locationType) {
        for (LocationType value : LocationType.values()) {
            if (value.toString().equals(locationType)) {
                return value;
            }
        }
        throw new CocosException(FailMessage.NOT_FOUND_LOCATION_TYPE);
    }
}
