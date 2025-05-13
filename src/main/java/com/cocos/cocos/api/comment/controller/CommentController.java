package com.cocos.cocos.api.comment.controller;

import com.cocos.cocos.api.comment.dto.request.CommentContentRequest;
import com.cocos.cocos.api.comment.dto.request.SubCommentContentRequest;
import com.cocos.cocos.api.comment.dto.response.CommentsAndSubCommentsResponse;
import com.cocos.cocos.api.comment.dto.response.MyAllCommentsResponse;
import com.cocos.cocos.api.comment.service.CommentService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.util.validation.EntityExistsValidator;
import com.cocos.cocos.validation.post.PostIdConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController implements CommentControllerSwagger {

    private final CommentService commentService;
    private final EntityExistsValidator entityExistsValidator;

    @PostMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> addPostComment(
            @PathVariable(name = "postId") @PostIdConstraint final Long postId,
            @RequestBody final CommentContentRequest commentContentRequest
    ) {
        entityExistsValidator.validatePetByMemberId(PrincipalHandler.getMemberIdFromPrincipal());
        commentService.addPostComment(postId, commentContentRequest.content(), PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<BaseResponse<Void>> deletePostComment(
            @PathVariable(name = "commentId") final Long commentId
    ) {
        entityExistsValidator.validateCommentByCommentId(commentId);
        commentService.deletePostComment(commentId, PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @PostMapping("/sub/{commentId}")
    public ResponseEntity<BaseResponse<Void>> addPostSubComment(
            @PathVariable(name = "commentId") final Long commentId,
            @RequestBody final SubCommentContentRequest subCommentContentRequest
    ) {
        entityExistsValidator.validateCommentByCommentId(commentId);
        entityExistsValidator.validateMemberByNickname(subCommentContentRequest.nickname());
        entityExistsValidator.validatePetByMemberId(PrincipalHandler.getMemberIdFromPrincipal());
        commentService.addPostSubComment(commentId, subCommentContentRequest.nickname(), subCommentContentRequest.content(), PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

    @DeleteMapping("/sub/{subCommentId}")
    public ResponseEntity<BaseResponse<Void>> deletePostSubComment(
            @PathVariable(name = "subCommentId") final Long subCommentId
    ) {
        entityExistsValidator.validateSubCommentBySubCommentId(subCommentId);
        commentService.deletePostSubComment(subCommentId, PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<CommentsAndSubCommentsResponse>> getPostComments(
            @PathVariable(name = "postId") final Long postId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, commentService.getPostComments(postId, PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @GetMapping("/members")
    public ResponseEntity<BaseResponse<MyAllCommentsResponse>> getMemberComments(
            @RequestParam(name = "nickname", required = false) final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK, commentService.getMemberComments(nickname, PrincipalHandler.getMemberIdFromPrincipal()));
    }
}
