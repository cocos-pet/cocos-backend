package com.cocos.cocos.api.member.dto.response;

public record NicknameExistenceResponse(
        boolean isExistNickname
) {
    public static NicknameExistenceResponse of(final boolean isExistNickname) {
        return new NicknameExistenceResponse(isExistNickname);
    }
}
