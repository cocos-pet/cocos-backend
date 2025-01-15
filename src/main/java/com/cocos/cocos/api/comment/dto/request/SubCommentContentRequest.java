package com.cocos.cocos.api.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SubCommentContentRequest(
        @Schema(description = "언급된 멤버의 아이디 정보입니다. ", example = "1")
        Long mentionedMemberId,
        @Schema(description = "댓글 내용", example = "댓글의 내용입니다. ")
        String content
) {
    public static SubCommentContentRequest of(final Long mentionedMemberId, final String content) {
        return new SubCommentContentRequest(mentionedMemberId, content);
    }
}
