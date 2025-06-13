package com.cocos.cocos.api.member.dto.request;

import com.cocos.cocos.enums.location.LocationType;
import com.cocos.cocos.validation.location.HasLocation;
import com.cocos.cocos.validation.location.LocationConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@LocationConstraint
public record ProfileUpdateRequest(
        @Schema(description = "닉네임", example = "코코스")
        String nickname,
        @Schema(description = "주소", example = "~시 ~구 ~동")
        String address,
        @Schema(description = "도로명 주소", example = "~시 ~로 ~번길")
        String roadAddress,
        @Schema(description = "시/도 이름", example = "경기도")
        String cityName,
        @Schema(description = "시/군/구", example = "평택시")
        String districtName,
        @Schema(description = "읍/면/동", example = "비전2동")
        String townName,
        @Schema(description = "위도", example = "35.xxxx")
        Double latitude,
        @Schema(description = "경도", example = "128.xxx")
        Double longitude,
        @Schema(description = "위치 아이디", example = "1")
        Long locationId,
        @Schema(description = "위치 종류", example = "CITY | DISTRICT")
        LocationType locationType
) implements HasLocation {
}
