package com.cocos.cocos.api.comment.dto.response;

import java.util.List;

public record CommentsAndSubCommentsResponse(
        List<CommentAndSubCommentsResponse> comments
) {
    public static CommentsAndSubCommentsResponse of(final List<CommentAndSubCommentsResponse> commentsAndSubComments) {
        return new CommentsAndSubCommentsResponse(List.copyOf(commentsAndSubComments));
    }
}

