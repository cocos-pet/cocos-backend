package com.cocos.cocos.api.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MyAllCommentsResponse(
        @Schema(description = "댓글 리스트")
        List<MyCommentResponse> comments,
        @Schema(description = "대댓글 리스트")
        List<MySubCommentResponse> subComments
) {
    public static MyAllCommentsResponse of(final List<MyCommentResponse> comments, final List<MySubCommentResponse> subComments) {
        return new MyAllCommentsResponse(comments, subComments);
    }
}
