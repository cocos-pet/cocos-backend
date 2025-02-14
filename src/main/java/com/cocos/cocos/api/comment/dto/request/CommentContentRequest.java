package com.cocos.cocos.api.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommentContentRequest(
        @Schema(description = "댓글 내용", example = "댓글의 내용입니다. ")
        String content
) {
    //ToDo: 이 부분은 없어도 될 것 같음. request를 직접 만드는 경우가 없음
    public static CommentContentRequest of(final String content) {
        return new CommentContentRequest(content);
    }
}
