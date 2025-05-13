package com.cocos.cocos.api.post.dto.request;

import com.cocos.cocos.validation.category.CategoryIdConstraint;
import com.cocos.cocos.validation.disease.DiseaseIdsConstraint;
import com.cocos.cocos.validation.symptom.SymptomIdsConstraint;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostRequest(
        @Schema(description = "게시글 카테고리 아이디", example = "1")
        @CategoryIdConstraint
        Long categoryId,
        @Schema(description = "게시글 제목", example = "제목")
        String title,
        @Schema(description = "게시글 내용", example = "내용")
        String content,
        @Schema(description = "게시글 이미지 리스트", example = "[image1.png, image2.jpg]")
        List<String> images,
        @Schema(description = "게시글 동물 종 아이디", example = "1")
        Long animalId,
        @Schema(description = "게시글 증상 아이디 리스트", example = "[1, 2]")
        @SymptomIdsConstraint
        List<Long> symptomIds,
        @Schema(description = "게시글 질병 아이디 리스트", example = "[2, 4]")
        @DiseaseIdsConstraint
        List<Long> diseaseIds
) {
}
