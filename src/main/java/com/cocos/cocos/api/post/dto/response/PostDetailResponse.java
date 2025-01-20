package com.cocos.cocos.api.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailResponse(
        @Schema(description = "사용자 닉네임", example = "하윙")
        String nickname,
        @Schema(description = "사용자 프로필 이미지", example = "하윙")
        String profileImage,
        @Schema(description = "품종 이름", example = "포메라니안")
        String breed,
        @Schema(description = "나이", example = "1")
        int petAge,
        @Schema(description = "공감 갯수", example = "10")
        int likeCounts,
        @Schema(description = "댓글 갯수", example = "20")
        int totalCommentCounts,
        @Schema(description = "게시글 제목", example = "제목")
        String title,
        @Schema(description = "게시글 내용", example = "내용")
        String content,
        @Schema(description = "이미지 목록", example = "[https://image, https://image2]")
        List<String> images,
        @Schema(description = "게시글 카테고리", example = "질병/증상")
        String category,
        @Schema(description = "게시글 태그", example = "[심장병, 눈이 건조함]")
        List<String> tags,
        @Schema(description = "생성일", example = "yyyy-mm-dd:hh-mm-ss~")
        LocalDateTime createdAt,
        @Schema(description = "수정일", example = "yyyy-mm-dd:hh-mm-ss~")
        LocalDateTime updatedAt,
        @Schema(description = "좋아요 클릭 여부", example = "true")
        boolean isLiked
) {
}
