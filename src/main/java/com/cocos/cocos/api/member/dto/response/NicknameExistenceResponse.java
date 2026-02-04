package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record NicknameExistenceResponse(
        @Schema(description = "닉네임 존재 여부 조회", example = "true")
        boolean isExistNickname
) {
    public static NicknameExistenceResponse of(final boolean isExistNickname) {
        return new NicknameExistenceResponse(isExistNickname);
    }
}
