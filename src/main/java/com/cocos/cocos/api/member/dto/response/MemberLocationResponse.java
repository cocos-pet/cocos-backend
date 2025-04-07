package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 위치 응답 예시", example = "{\"townId\": 1, \"townName\": \"논현동\"}")
public record MemberLocationResponse(
        @Schema(description = "동 아이디", example = "1")
        Long townId,

        @Schema(description = "동 이름", example = "논현동")
        String townName
) {
    public static MemberLocationResponse of(final Long townId, final String townName) {
        return new MemberLocationResponse(townId, townName);
    }
}
