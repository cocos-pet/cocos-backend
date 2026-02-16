package com.cocos.cocos.api.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record CommentAndSubCommentsResponse(
        @Schema(description = "댓글 아이디", example = "1")
        Long id,
        @Schema(description = "댓글 단 사용자의 닉네임", example = "닉네임")
        String nickname,
        @Schema(description = "댓글 단 사용자의 프로필 이미지 주소", example = "https://")
        String profileImage,
        @Schema(description = "댓글 단 사용자의 반려동물 종", example = "포메라니안")
        String breed,
        @Schema(description = "반려동물 나이", example = "11")
        int petAge,
        @Schema(description = "댓글 내용", example = "요즘 ~~고민ㄴ이..")
        String content,
        @Schema(description = "댓글 생성일", example = "2025-01-01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "작성자여부", example = "true")
        boolean isWriter,
        @Schema(description = "게시글 작성자 여부", example = "false")
        boolean isPostWriter,
        @Schema(description = "대댓글 리스트")
        List<SubCommentResponse> subComments
) {
    public static CommentAndSubCommentsResponse of(final Long id, final String nickname, final String profileImage, final String breed, final int petAge, final String content, final LocalDateTime createdAt, final boolean isWriter, final boolean isPostWriter, final List<SubCommentResponse> subComments) {
        return new CommentAndSubCommentsResponse(id, nickname, profileImage, breed, petAge, content, createdAt, isWriter, isPostWriter, List.copyOf(subComments));
    }
}
