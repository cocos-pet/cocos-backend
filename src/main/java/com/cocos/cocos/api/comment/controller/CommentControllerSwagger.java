package com.cocos.cocos.api.comment.controller;

import com.cocos.cocos.api.comment.dto.response.CommentsAndSubCommentsResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Comment Controller", description = "댓글 관련 API")
public interface CommentControllerSwagger {

    @Operation(summary = "게시글 댓글&대댓글 조회 API", description = "게시글에 달린 댓글과 대댓글을 조회하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다.")
    @Parameter(name = "postId", description = "게시글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<CommentsAndSubCommentsResponse>> getPostComments(final Long postId);
}
