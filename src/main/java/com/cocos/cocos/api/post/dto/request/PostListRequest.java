package com.cocos.cocos.api.post.dto.request;

import com.cocos.cocos.enums.post.PostSortCriteria;
import com.cocos.cocos.validation.category.CategoryIdConstraint;
import com.cocos.cocos.validation.disease.DiseaseIdsConstraint;
import com.cocos.cocos.validation.post.PostIdConstraint;
import com.cocos.cocos.validation.symptom.SymptomIdsConstraint;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record PostListRequest(
        @Schema(description = "검색 키워드", example = "포메 or null")
        String keyword,
        @Schema(description = "동물 아이디 리스트", example = "[1, 2] or null")
        List<Long> animalIds,
        @Schema(description = "증상 아이디 리스트", example = "[1, 2] or null")
        @SymptomIdsConstraint
        List<Long> symptomIds,
        @Schema(description = "질병 아이디 리스트", example = "[1, 2] or null")
        @DiseaseIdsConstraint
        List<Long> diseaseIds,
        @Schema(description = "정렬 기준", example = "RECENT or POPULAR")
        PostSortCriteria sortBy,
        @Schema(description = "마지막 게시글 아이디", example = "1 or null")
        @PostIdConstraint
        Long cursorId,
        @Schema(description = "마지막 게시글 카테고리 아이디", example = "1 or null")
        @CategoryIdConstraint
        Long categoryId,
        @Schema(description = "마지막 게시글 좋아요 수 아이디", example = "1 or null")
        Long likeCount,
        @Schema(description = "마지막 게시글 생성일", example = "YYYY-MM-DD~ or null")
        LocalDateTime createdAt,
        @Schema(description = "신체 아이디", example = "1")
        Long bodyId
) {
}
