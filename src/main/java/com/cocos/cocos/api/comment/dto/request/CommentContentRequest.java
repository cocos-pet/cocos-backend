package com.cocos.cocos.api.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommentContentRequest(
        @Schema(description = "댓글 내용", example = "댓글의 내용입니다. ")
        String content
) {
}
