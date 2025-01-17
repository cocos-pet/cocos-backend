package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberPostDetailResponse(
        @Schema(description = "게시글 아이디", example = "1")
        Long id,
        String nickname,
        @Schema(description = "게시글 제목", example = "title")
        String title,
        @Schema(description = "게시글 내용", example = "content")
        String content,
        @Schema(description = "게시글 좋아요 개수", example = "1")
        int likeCount,
        @Schema(description = "게시글 댓글 개수", example = "1")
        int commentCount,
        @Schema(description = "게시글 생성일", example = "1")
        LocalDateTime createdAt,
        @Schema(description = "게시글 수정일", example = "1")
        LocalDateTime updatedAt,
        @Schema(description = "게시글 이미지", example = "1")
        String image,
        @Schema(description = "게시글 카테고리", example = "1")
        String category,
        @Schema(description = "반려동물 품종", example = "포메라니안")
        String breed,
        @Schema(description = "반려동물 나이", example = "14")
        int age
) {
}
