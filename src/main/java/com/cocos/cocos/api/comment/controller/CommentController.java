package com.cocos.cocos.api.comment.controller;

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

    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<CommentsAndSubCommentsResponse>> getPostComments(
            @PathVariable(name = "postId") final Long postId
    ) {
        final CommentsAndSubCommentsResponse postComments = commentService.getPostComments(postId, memberId);
        return SuccessResponse.success(SuccessMessage.OK, postComments);
    }
}
