package com.cocos.cocos.api.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MyCommentResponse(
        @Schema(description = "댓글 아이디", example = "1")
        Long id,
        @Schema(description = "댓글 내용", example = "content")
        String content,
        @Schema(description = "게시글 아이디", example = "1")
        Long postId,
        @Schema(description = "게시글 제목", example = "오늘 무슨 일이..")
        String postTitle,
        @Schema(description = "댓글 생성일", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt
) {
    //ToDo: cmd + option + L로 항상 정렬 필요
    public static MyCommentResponse of(final Long id, final String content, final Long postId, final String postTitle, final LocalDateTime createdAt) {
        return new MyCommentResponse(id, content, postId, postTitle, createdAt);
    }
}
