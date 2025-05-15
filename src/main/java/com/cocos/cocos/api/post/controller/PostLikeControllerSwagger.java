package com.cocos.cocos.api.post.controller;

import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.validation.post.PostIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "PostLike Controller", description = "게시글 공감 관련 API")
public interface PostLikeControllerSwagger {

    @Operation(summary = "게시글 공감 추가 API", description = "게시글 공감을 추가하는 API입니다.")
    @ApiResponse(
            responseCode = "204",
            description = "게시글 공감 추가 성공")
    @Parameter(name = "postId", description = "게시글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<Void>> addPostLike(
            @PostIdConstraint final Long postId
    );

    @Operation(summary = "게시글 공감 삭제 API", description = "게시글 공감을 삭제하는 API입니다.")
    @ApiResponse(
            responseCode = "204",
            description = "게시글 공감 삭제 성공")
    @Parameter(name = "postId", description = "게시글 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<Void>> deletePostLike(
            @PostIdConstraint final Long postId
    );
}
