package com.cocos.cocos.api.member.dto.response;

public record NicknameExistenceResponse(
        //ToDo: 스키마 필요
        boolean isExistNickname
) {
    public static NicknameExistenceResponse of(final boolean isExistNickname) {
        return new NicknameExistenceResponse(isExistNickname);
    }
}
