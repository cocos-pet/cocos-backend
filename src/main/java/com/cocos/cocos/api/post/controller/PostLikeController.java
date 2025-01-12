package com.cocos.cocos.api.post.controller;

import com.cocos.cocos.api.post.service.PostLikeService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dev/likes")
@RequiredArgsConstructor
public class PostLikeController implements PostLikeControllerSwagger {

    private static final Long MEMBER_ID = 1L;
    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> addPostLike(
            @PathVariable(name = "postId") final Long postId
    ) {
        postLikeService.addPostLike(MEMBER_ID, postId);
        return SuccessResponse.success(SuccessMessage.NO_CONTENT, null);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePostLike(
            @PathVariable(name = "postId") final Long postId
    ) {
        postLikeService.deletePostLike(MEMBER_ID, postId);
        return SuccessResponse.success(SuccessMessage.NO_CONTENT, null);
    }
}
