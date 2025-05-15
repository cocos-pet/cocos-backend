package com.cocos.cocos.api.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MemberAllCommentsResponse(
        @Schema(description = "댓글 리스트")
        List<MemberCommentResponse> comments,
        @Schema(description = "대댓글 리스트")
        List<MemberSubCommentResponse> subComments
) {
    public static MemberAllCommentsResponse of(final List<MemberCommentResponse> comments, final List<MemberSubCommentResponse> subComments) {
        return new MemberAllCommentsResponse(comments, subComments);
    }
}
