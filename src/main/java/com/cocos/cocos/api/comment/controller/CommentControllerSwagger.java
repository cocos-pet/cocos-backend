package com.cocos.cocos.api.comment.controller;

import com.cocos.cocos.api.comment.dto.request.CommentContentRequest;
import com.cocos.cocos.api.comment.dto.request.SubCommentContentRequest;
import com.cocos.cocos.api.comment.dto.response.CommentsAndSubCommentsResponse;
import com.cocos.cocos.api.comment.dto.response.MyAllCommentsResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Comment Controller", description = "댓글 관련 API")
public interface CommentControllerSwagger {

    @Operation(summary = "게시글 댓글 추가 API", description = "게시글에 댓글을 추가하는 API 입니다.")
    @ApiResponse(
            responseCode = "201",
            description = "요청에 성공했습니다.")
    public ResponseEntity<BaseResponse<Void>> addPostComment(
            @Parameter(name = "postId", description = "게시글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long")) final Long postId,
            @RequestBody final CommentContentRequest content
    );

    @Operation(summary = "게시글 댓글 삭제 API", description = "게시글의 댓글을 삭제하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다. ")
    @Parameter(name = "commentId", description = "댓글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<Void>> deletePostComment(
            final Long commentId
    );

    @Operation(summary = "게시글 대댓글 추가 API", description = "게시글에 대댓글을 추가하는 API 입니다.")
    @ApiResponse(
            responseCode = "201",
            description = "요청에 성공했습니다.")
    public ResponseEntity<BaseResponse<Void>> addPostSubComment(
            @Parameter(name = "commentId", description = "댓글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long")) final Long commentId,
            @RequestBody final SubCommentContentRequest content
    );

    @Operation(summary = "게시글 대댓글 삭제 API", description = "게시글의 대댓글을 삭제하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다. ")
    @Parameter(name = "subCommentId", description = "대댓글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<Void>> deletePostSubComment(
            final Long subCommentId
    );

    @Operation(summary = "게시글 댓글&대댓글 조회 API", description = "게시글에 달린 댓글과 대댓글을 조회하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다.")
    @Parameter(name = "postId", description = "게시글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<CommentsAndSubCommentsResponse>> getPostComments(final Long postId);

    @Operation(summary = "사용자 댓글 & 대댓글 조회 API", description = "사용자의 댓글과 대댓글을 조회하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다.")
    public ResponseEntity<BaseResponse<MyAllCommentsResponse>> getMemberComments(
            @RequestParam final String nickname
    );
}
