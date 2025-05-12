package com.cocos.cocos.api.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberHospitalResponse(
        @Schema(description = "병원 아이디", example = "1")
        Long id,
        @Schema(description = "병원 이름", example = "코코스 동물병원")
        String name,
        @Schema(description = "병원 주소", example = "서울시 강남구 테헤란로")
        String address,
        @Schema(description = "병원 이미지", example = "https://~")
        String image
) {
    public static MemberHospitalResponse of(final Long id, final String name, final String address, final String image) {
        return new MemberHospitalResponse(id, name, address, image);
    }
}
