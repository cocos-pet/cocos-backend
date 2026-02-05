package com.cocos.cocos.api.review.dto.request;

import com.cocos.cocos.common.datetime.DateParser;
import com.cocos.cocos.enums.pet.Gender;
import com.cocos.cocos.validation.breed.BreedIdConstraint;
import com.cocos.cocos.validation.disease.DiseaseIdConstraint;
import com.cocos.cocos.validation.purpose.PurposeIdConstraint;
import com.cocos.cocos.validation.review.BadReviewIdsConstraint;
import com.cocos.cocos.validation.review.GoodReviewIdsConstraint;
import com.cocos.cocos.validation.symptom.SymptomIdsConstraint;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewAddRequest(
        @Schema(description = "종 아이디", nullable = true, example = "1")
        @BreedIdConstraint
        Long breedId,
        @Schema(description = "성별", nullable = true, example = "F | M")
        Gender gender,
        @Schema(description = "몸무게", nullable = true, example = "5")
        Double weight,
        @Schema(description = "방문 날짜", nullable = true, example = "2025.04.22")
        String visitedAt,
        @Schema(description = "내용", nullable = true, example = "병원 시설이 너무 깔끔해요.")
        String content,
        @Schema(description = "방문 목적 아이디", nullable = true, example = "1")
        @PurposeIdConstraint
        Long purposeId,
        @Schema(description = "질병 아이디", nullable = true, example = "7")
        @DiseaseIdConstraint
        Long diseaseId,
        @Schema(description = "증상 아이디 리스트", nullable = true, example = "[5,30...]")
        @SymptomIdsConstraint
        List<Long> symptomIds,
        @Schema(description = "좋은 간단 리뷰 아이디 리스트", nullable = true, example = "[1,2,3...]")
        @GoodReviewIdsConstraint
        List<Long> goodReviewIds,
        @Schema(description = "나쁜 간단 리뷰 아이디 리스트", nullable = true, example = "[1,2,3...]")
        @BadReviewIdsConstraint
        List<Long> badReviewIds,
        @Schema(description = "리뷰 이미지 리스트", nullable = true, example = "[image1, image2...]")
        List<String> images
) {
        public LocalDateTime parsedVisitedAt() {
                return DateParser.parseToLocalDateTime(visitedAt);
        }
}
