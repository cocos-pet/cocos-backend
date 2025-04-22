package com.cocos.cocos.api.member.constant;

import com.cocos.cocos.enums.location.LocationType;

public final class MemberDefaults {

    private MemberDefaults() {
    }

    public static final String MEMBER_BASE_IMAGE_URL = "member/baseProfileImage.png";
    public static final Long DEFAULT_LOCATION_ID = 1L;
    public static final String DEFAULT_ADDRESS = "주소를 설정해주세요!";
    public static final String DEFAULT_ROAD_ADDRESS = "도로명 주소를 설정해주세요!";
    public static final Double DEFAULT_LATITUDE = 0.0;
    public static final Double DEFAULT_LONGITUDE = 0.0;
    public static final LocationType DEFAULT_LOCATION_TYPE = LocationType.DISTRICT;
}
