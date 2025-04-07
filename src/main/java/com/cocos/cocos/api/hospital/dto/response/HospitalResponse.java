package com.cocos.cocos.api.hospital.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record HospitalResponse(
        @Schema(description = "병원 아이디", example = "1")
        Long id,
        @Schema(description = "병원명", example = "코코스동물병원")
        String name,
        @Schema(description = "주소", example = "서울시 강남구")
        String address,
        @Schema(description = "리뷰수", example = "777")
        int reviewCount,
        @Schema(description = "이미지", example = "이미지 url")
        String image
) {
    public static HospitalResponse of(final Long id, final String name, final String address, final int reviewCount, final String image) {
        return new HospitalResponse(id, name, address, reviewCount, image);
    }
}
