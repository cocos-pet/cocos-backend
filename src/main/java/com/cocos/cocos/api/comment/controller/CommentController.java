package com.cocos.cocos.api.comment.controller;

import com.cocos.cocos.api.comment.dto.request.CommentContentRequest;
import com.cocos.cocos.api.comment.dto.response.CommentsAndSubCommentsResponse;
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

    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePostComment(
            @PathVariable(name = "postId") final Long postId
    ) {
        commentService.deletePostComment(postId, memberId);
        return SuccessResponse.success(SuccessMessage.OK,null);
    }

    @PostMapping("/sub/{commentId}")
    public ResponseEntity<BaseResponse<Void>> addPostSubComment(
            @PathVariable(name = "commentId") final Long commentId,
            @RequestBody final CommentContentRequest body
    ) {
        commentService.addPostSubComment(commentId, body.content(), memberId);
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<CommentsAndSubCommentsResponse>> getPostComments(
            @PathVariable(name = "postId") final Long postId
    ) {
        final CommentsAndSubCommentsResponse postComments = commentService.getPostComments(postId, memberId);
        return SuccessResponse.success(SuccessMessage.OK, postComments);
    }
}
