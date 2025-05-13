package com.cocos.cocos.api.comment.dto.request;

import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import io.swagger.v3.oas.annotations.media.Schema;

public record SubCommentContentRequest(
        @Schema(description = "언급된 멤버의 닉네임 정보입니다. ", example = "nickname")
        @MemberNicknameConstraint
        String nickname,
        @Schema(description = "댓글 내용", example = "댓글의 내용입니다. ")
        String content
) {
}
