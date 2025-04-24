package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberHospitalResponse(
        @Schema(description = "병원 아이디", example = "1")
        Long id,

        @Schema(description = "병원 이름", example = "코코스 동물병원")
        String name,

        @Schema(description = "병원 주소", example = "서울시 강남구 테헤란로")
        String address
) {
    public static MemberHospitalResponse of(final Long id, final String name, final String address) {
        return new MemberHospitalResponse(id, name, address);
    }
}
