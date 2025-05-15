package com.cocos.cocos.api.post.controller;

import com.cocos.cocos.api.post.dto.request.PostListRequest;
import com.cocos.cocos.api.post.dto.request.PostRequest;
import com.cocos.cocos.api.post.dto.response.*;
import com.cocos.cocos.api.post.service.PostService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import com.cocos.cocos.validation.post.PostIdConstraint;
import jakarta.validation.Valid;
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
            @PathVariable(name = "postId") @PostIdConstraint final Long postId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, postService.getPostDetail(postId, PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable(name = "postId") @PostIdConstraint final Long postId
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
            @RequestBody @Valid final PostRequest postRequest
    ) {
        return SuccessResponse.success(SuccessMessage.OK, postService.addPost(
                PrincipalHandler.getMemberIdFromPrincipal(), postRequest.categoryId(), postRequest.title(),
                postRequest.content(), postRequest.images(), postRequest.animalId(),
                postRequest.symptomIds(), postRequest.diseaseIds()
        ));
    }

    @GetMapping("/popular")
    public ResponseEntity<BaseResponse<PopularPostsResponse>> getPopularPosts() {
        return SuccessResponse.success(SuccessMessage.OK, postService.getPopularPosts(PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @PostMapping("/filters")
    public ResponseEntity<BaseResponse<PostListResponse>> getPosts(
            @RequestBody @Valid final PostListRequest postListRequest
    ) {
        return SuccessResponse.success(SuccessMessage.OK, postService.getPosts(PrincipalHandler.getMemberIdFromPrincipal(), postListRequest.keyword(),
                postListRequest.animalIds(), postListRequest.symptomIds(), postListRequest.diseaseIds(),
                postListRequest.sortBy(), postListRequest.cursorId(), postListRequest.categoryId(),
                postListRequest.likeCount(), postListRequest.createdAt(), postListRequest.bodyId()));
    }

    @GetMapping("/members")
    public ResponseEntity<BaseResponse<MemberPostsResponse>> getMemberPosts(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK, postService.getMemberPosts(PrincipalHandler.getMemberIdFromPrincipal(), nickname));
    }
}
