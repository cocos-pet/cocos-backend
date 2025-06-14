package com.cocos.cocos.api.hospital.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record HospitalDetailResponse(
        @Schema(description = "병원 이름", example = "코코스 동물병원")
        String name,
        @Schema(description = "병원 전화번호", example = "02-1234-5671")
        String phoneNumber,
        @Schema(description = "병원 태그", example = "[강아지, 심장병]")
        List<String> tags,
        @Schema(description = "병원 소개", example = "이 동물병원은 지상 최고의 동물병원입니다.")
        String introduction,
        @Schema(description = "병원 주소", example = "서울특별시 강남구 논현동")
        String address,
        @Schema(description = "병원 이미지", example = "https://www.~~")
        String image,
        @Schema(description = "병원 키워드들", example = "#친절함, #강아지")
        String keywords,
        @Schema(description = "병원 홈페이지 URL", example = "https://www.~~")
        String homepageUrl
) {
    public static HospitalDetailResponse of(final String name, final String phoneNumber, final List<String> tags, final String introduction, final String address, final String image, final String keywords, final String homepageUrl) {
        return new HospitalDetailResponse(name, phoneNumber, tags, introduction, address, image, keywords, homepageUrl);
    }
}
