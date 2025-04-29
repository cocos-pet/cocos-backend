package com.cocos.cocos.external.login.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUnlinkRequest(
        String targetIdType,
        Long targetId
) {
    public static KakaoUnlinkRequest of(final String targetIdType, final Long targetId) {
        return new KakaoUnlinkRequest(targetIdType, targetId);
    }
}
