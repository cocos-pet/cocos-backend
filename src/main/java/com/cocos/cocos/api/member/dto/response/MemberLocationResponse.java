package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 위치 응답 예시", example = "{\"townId\": 1, \"townName\": \"논현동\"}")
public record MemberLocationResponse(
        @Schema(description = "위치 아이디", example = "1")
        Long locationId,
        @Schema(description = "위치 이름", example = "강남구")
        String locationName,
        @Schema(description = "위치 타입", example = "CITY | DISTRICT")
        String locationType
) {
    public static MemberLocationResponse of(final Long locationId, final String locationName, final String locationType) {
        return new MemberLocationResponse(locationId, locationName, locationType);
    }
}
