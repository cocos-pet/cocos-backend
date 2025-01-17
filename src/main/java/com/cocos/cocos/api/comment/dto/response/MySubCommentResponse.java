package com.cocos.cocos.api.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MySubCommentResponse(
        @Schema(description = "대댓글 아이디", example = "1")
        Long id,
        @Schema(description = "대댓글 내용", example = "content")
        String content,
        @Schema(description = "게시글 아이디", example = "1")
        Long postId,
        @Schema(description = "게시글 제목", example = "오늘 무슨 일이..")
        String postTitle,
        @Schema(description = "대댓글 생성일", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "언급 표시를 위한 사용자 닉네임", example = "빵빵이")
        String mentionedNickname
) {
    public static MySubCommentResponse of(final Long id, final String content, final Long postId, final String postTitle, final LocalDateTime createdAt, final String mentionedNickname) {
        return new MySubCommentResponse(id, content, postId, postTitle, createdAt, mentionedNickname);
    }
}
