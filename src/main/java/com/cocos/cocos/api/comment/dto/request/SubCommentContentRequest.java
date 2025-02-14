package com.cocos.cocos.api.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SubCommentContentRequest(
        @Schema(description = "언급된 멤버의 닉네임 정보입니다. ", example = "nickname")
        String nickname,
        @Schema(description = "댓글 내용", example = "댓글의 내용입니다. ")
        String content
) {
    //ToDo: 이 부분은 없어도 될 것 같음. request를 직접 만드는 경우가 없음
    public static SubCommentContentRequest of(final String nickname, final String content) {
        return new SubCommentContentRequest(nickname, content);
    }
}
