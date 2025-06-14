package com.cocos.cocos.validation.location;

import com.cocos.cocos.enums.location.LocationType;

public interface HasLocation {
    LocationType locationType();
    Long locationId();
}
