package com.cocos.cocos.api.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PostRequest(
        @Schema(description = "게시글 카테고리 아이디", example = "1")
        Long categoryId,
        @Schema(description = "게시글 제목", example = "제목")
        String title,
        @Schema(description = "게시글 내용", example = "내용")
        String content,
        @Schema(description = "게시글 이미지 리스트", example = "[image1.png, image2.jpg]")
        List<String> images,
        @Schema(description = "게시글 카테고리 아이디", example = "1")
        Long animalId,
        @Schema(description = "게시글 카테고리 아이디", example = "[1, 2]")
        List<Long> symptomIds,
        @Schema(description = "게시글 카테고리 아이디", example = "[2, 4]")
        List<Long> diseaseIds
) {
}
