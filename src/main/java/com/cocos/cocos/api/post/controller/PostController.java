package com.cocos.cocos.api.post.controller;

import com.cocos.cocos.api.post.dto.response.PostCategoriesResponse;
import com.cocos.cocos.api.post.dto.response.PostDetailResponse;
import com.cocos.cocos.api.post.service.PostService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/posts")
@RequiredArgsConstructor
public class PostController implements PostControllerSwagger {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<BaseResponse<PostDetailResponse>> getPostDetail(
            @PathVariable(name = "postId") final Long postId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, postService.getPostDetail(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable(name = "postId") final Long postId
    ) {
        postService.deletePost(postId);
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<PostCategoriesResponse>> getPostCategories() {
        return SuccessResponse.success(SuccessMessage.OK, postService.getCategories());
    }
}
