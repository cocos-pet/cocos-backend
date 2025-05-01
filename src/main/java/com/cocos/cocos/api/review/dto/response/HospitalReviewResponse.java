package com.cocos.cocos.api.review.dto.response;

import com.cocos.cocos.enums.pet.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "리뷰 정보")
public record HospitalReviewResponse(
        @Schema(description = "리뷰 ID")
        Long id,
        @Schema(description = "병원 아이디")
        Long hospitalId,
        @Schema(description = "병원 이름")
        String hospitalName,
        @Schema(description = "방문 일자")
        String visitedAt,
        @Schema(description = "병원 주소")
        String hospitalAddress,
        @Schema(description = "리뷰 본문")
        String content,
        @Schema(description = "리뷰 요약 옵션 리스트")
        ReviewSummaryOptionListResponse reviewSummary,
        @Schema(description = "리뷰 이미지 리스트")
        List<String> images,
        @Schema(description = "증상 리스트")
        List<String> symptoms,
        @Schema(description = "질병 이름")
        String disease,
        @Schema(description = "동물 이름")
        String animal,
        @Schema(description = "성별")
        Gender gender,
        @Schema(description = "종 이름")
        String breed,
        @Schema(description = "몸무게")
        double weight
) {
    public static HospitalReviewResponse of(
            final Long id, final Long hospitalId, final String hospitalName, final String visitedAt, final String hospitalAddress,
            final String content, final ReviewSummaryOptionListResponse reviewSummary,
            final List<String> images, final List<String> symptoms, final String disease,
            final String animal, final Gender gender, final String breed, final double weight
    ) {
        return new HospitalReviewResponse(id, hospitalId, hospitalName, visitedAt, hospitalAddress,
                content, reviewSummary, images, symptoms,
                disease, animal, gender, breed, weight);
    }
}
