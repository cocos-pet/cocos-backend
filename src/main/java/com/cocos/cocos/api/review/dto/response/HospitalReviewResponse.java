package com.cocos.cocos.api.review.dto.response;

import com.cocos.cocos.enums.pet.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "리뷰 정보")
public record HospitalReviewResponse(
        @Schema(description = "리뷰 ID", example = "1")
        Long id,
        @Schema(description = "작성자 아이디", example = "1")
        Long memberId,
        @Schema(description = "작성자 닉네임", example = "냥냥")
        String nickname,
        @Schema(description = "종 이름", example = "말티즈")
        String memberBreed,
        @Schema(description = "펫 나이", example = "1")
        int age,
        @Schema(description = "병원 아이디", example = "1")
        Long hospitalId,
        @Schema(description = "병원 이름", example = "코코병원")
        String hospitalName,
        @Schema(description = "방문 일자", example = "2020.02.02")
        String visitedAt,
        @Schema(description = "병원 주소", example = "서울시 강남구")
        String hospitalAddress,
        @Schema(description = "리뷰 본문", example = "좋았다.")
        String content,
        @Schema(description = "리뷰 요약 옵션 리스트")
        ReviewSummaryOptionListResponse reviewSummary,
        @Schema(description = "리뷰 이미지 리스트")
        List<String> images,
        @Schema(description = "증상 리스트", example = "배가 아파요")
        List<String> symptoms,
        @Schema(description = "질병 이름", example = "심장병")
        String disease,
        @Schema(description = "동물 이름", example = "강아지")
        String animal,
        @Schema(description = "성별", example = "F")
        Gender gender,
        @Schema(description = "종 이름", example = "말티즈")
        String breed,
        @Schema(description = "몸무게", example = "2.7")
        double weight
) {
    public static HospitalReviewResponse of(
            final Long id, final Long memberId, final String nickname, final String memberBreed, final int age, final Long hospitalId, final String hospitalName, final String visitedAt, final String hospitalAddress,
            final String content, final ReviewSummaryOptionListResponse reviewSummary,
            final List<String> images, final List<String> symptoms, final String disease,
            final String animal, final Gender gender, final String breed, final double weight
    ) {
        return new HospitalReviewResponse(id, memberId, nickname, memberBreed, age, hospitalId, hospitalName, visitedAt, hospitalAddress,
                content, reviewSummary, images, symptoms,
                disease, animal, gender, breed, weight);
    }
}
