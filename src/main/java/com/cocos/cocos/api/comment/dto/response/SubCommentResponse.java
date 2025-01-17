package com.cocos.cocos.api.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SubCommentResponse(
        @Schema(description = "대댓글 아이디",example = "1")
        Long id,
        @Schema(description = "대댓글 사용자 닉네임",example = "닉네임2")
        String nickname,
        @Schema(description = "대댓글 사용자 프로필 사진",example = "https://")
        String profileImage,
        @Schema(description = "대댓글 사용자 애완동물 종",example = "포메라니안")
        String breed,
        @Schema(description = "애완동물 나이", example = "12")
        int petAge,
        @Schema(description = "대댓글 내용", example = "~~좋아요")
        String content,
        @Schema(description = "생성일", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "작성자 여부",example = "true")
        boolean isWriter,
        @Schema(description = "언급 표시를 위한 사용자 닉네임", example = "빵빵이")
        String mentionedNickname
) {
    public static SubCommentResponse of(final Long id, final String nickname, final String profileImage, final String breed, final int petAge, final String content, LocalDateTime createdAt, final boolean isWriter, final String mentionedNickname) {
        return new SubCommentResponse(id, nickname, profileImage, breed, petAge, content, createdAt, isWriter, mentionedNickname);
    }
}
