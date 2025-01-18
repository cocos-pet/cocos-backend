package com.cocos.cocos.api.comment.controller;

import com.cocos.cocos.api.comment.dto.request.CommentContentRequest;
import com.cocos.cocos.api.comment.dto.request.SubCommentContentRequest;
import com.cocos.cocos.api.comment.dto.response.CommentsAndSubCommentsResponse;
import com.cocos.cocos.api.comment.dto.response.MyAllCommentsResponse;
import com.cocos.cocos.api.comment.service.CommentService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController implements CommentControllerSwagger {
    private static final Long memberId = 1L;
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> addPostComment(
            @PathVariable(name = "postId") final Long postId,
            @RequestBody final CommentContentRequest body
    ) {
        commentService.addPostComment(postId, body.content(), memberId);
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<BaseResponse<Void>> deletePostComment(
            @PathVariable(name = "commentId") final Long commentId
    ) {
        commentService.deletePostComment(commentId, memberId);
        return SuccessResponse.success(SuccessMessage.OK,null);
    }

    @PostMapping("/sub/{commentId}")
    public ResponseEntity<BaseResponse<Void>> addPostSubComment(
            @PathVariable(name = "commentId") final Long commentId,
            @RequestBody final SubCommentContentRequest body
    ) {
        commentService.addPostSubComment(commentId, body.mentionedMemberId(), body.content(), memberId);
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

    @DeleteMapping("/sub/{subCommentId}")
    public ResponseEntity<BaseResponse<Void>> deletePostSubComment(
            @PathVariable(name = "subCommentId") final Long subCommentId
    ) {
        commentService.deletePostSubComment(subCommentId, memberId);
        return SuccessResponse.success(SuccessMessage.OK,null);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<CommentsAndSubCommentsResponse>> getPostComments(
            @PathVariable(name = "postId") final Long postId
    ) {
        final CommentsAndSubCommentsResponse postComments = commentService.getPostComments(postId, memberId);
        return SuccessResponse.success(SuccessMessage.OK, postComments);
    }

    @GetMapping("/my")
    public ResponseEntity<BaseResponse<MyAllCommentsResponse>> getMyComments(
            @RequestParam(name = "nickname", required = false) final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK, commentService.getMyComments(nickname, memberId));
    }
}
