package com.cocos.cocos.api.post.controller;

import com.cocos.cocos.api.post.dto.request.PostListRequest;
import com.cocos.cocos.api.post.dto.request.PostRequest;
import com.cocos.cocos.api.post.dto.response.*;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import com.cocos.cocos.validation.post.PostIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Post Controller", description = "게시글 관련 API")
public interface PostControllerSwagger {

    @Operation(summary = "게시글 상세 조회 API", description = "게시글을 상세 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "게시글 상세 조회 성공")
    @Parameter(name = "postId", description = "게시글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<PostDetailResponse>> getPostDetail(@PostIdConstraint final Long postId);

    @Operation(summary = "게시글 삭제 API", description = "게시글을 삭제하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "게시글 삭제 성공")
    @Parameter(name = "postId", description = "게시글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<Void>> deletePost(@PostIdConstraint final Long postId);

    @Operation(summary = "게시글 카테고리 리스트 API", description = "게시글 카테고리 리스트를 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "게시글 카테고리 리스트 조회 성공")
    public ResponseEntity<BaseResponse<PostCategoriesResponse>> getPostCategories();

    @Operation(summary = "게시글 작성 가능 카테고리 리스트 API", description = "게시글 작성 가능 카테고리 리스트를 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "게시글 작성 가능 카테고리 리스트 조회 성공")
    public ResponseEntity<BaseResponse<PostCategoriesResponse>> getWritablePostCategories();

    @Operation(summary = "게시글 추가 API", description = "게시글을 추가하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "게시글 추가 성공")
    public ResponseEntity<BaseResponse<PostImagesResponse>> addPost(@RequestBody @Valid final PostRequest postRequest);

    @Operation(summary = "인기 게시글 조회 API", description = "인기 게시글을 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "인기 게시글 조회 성공")
    public ResponseEntity<BaseResponse<PopularPostsResponse>> getPopularPosts();

    @Operation(summary = "사용자 게시글 조회 API", description = "사용자 게시글을 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "사용자 게시글 조회 성공")
    @Parameter(name = "nickname", description = "모모", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "String"))
    public ResponseEntity<BaseResponse<MemberPostsResponse>> getMemberPosts(@MemberNicknameConstraint final String nickname);


    @Operation(summary = "게시글 리스트 조회 API", description = "사용자 게시글을 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "게시글 리스트 조회 성공")
    public ResponseEntity<BaseResponse<PostListResponse>> getPosts(
            @RequestBody @Valid final PostListRequest postListRequest
    );
}
