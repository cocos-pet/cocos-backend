package com.cocos.cocos.api.member.dto.request;

import com.cocos.cocos.enums.location.LocationType;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberProfileUpdateRequest(
        @Schema(description = "닉네임", example = "코코스")
        String nickname,
        @Schema(description = "위치 정보 종류", example = "CITY | DISTRICT | TOWN")
        LocationType locationType,
        @Schema(description = "위치 아이디", example = "3")
        Long townId
) {
}
