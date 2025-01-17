package com.cocos.cocos.api.post.controller;

import com.cocos.cocos.api.post.dto.request.PostListRequest;
import com.cocos.cocos.api.post.dto.request.PostRequest;
import com.cocos.cocos.api.post.dto.response.*;
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

    private static final Long MEMBER_ID = 1L;
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

    @PostMapping
    public ResponseEntity<BaseResponse<PostImagesResponse>> addPost(
            @RequestBody final PostRequest postRequest
    ) {
        return SuccessResponse.success(SuccessMessage.OK, postService.addPost(
                MEMBER_ID, postRequest.categoryId(), postRequest.title(),
                postRequest.content(), postRequest.images(), postRequest.animalId(),
                postRequest.symptomIds(), postRequest.diseaseIds()
        ));
    }

    @GetMapping("/popular")
    public ResponseEntity<BaseResponse<PopularPostsResponse>> getPopularPosts() {
        return SuccessResponse.success(SuccessMessage.OK, postService.getPopularPosts(MEMBER_ID));
    }

    @PostMapping("/filters")
    public ResponseEntity<BaseResponse<PostListResponse>> getPosts(
            @RequestBody final PostListRequest postListRequest
    ) {
        return SuccessResponse.success(SuccessMessage.OK, postService.getPosts(MEMBER_ID, postListRequest.keyword(),
                postListRequest.animalIds(), postListRequest.symptomIds(), postListRequest.diseaseIds(),
                postListRequest.sortBy(), postListRequest.cursorId(), postListRequest.categoryId(),
                postListRequest.likeCount(), postListRequest.createAt()));
    }
}
