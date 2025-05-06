package com.cocos.cocos.api.review.controller;

import com.cocos.cocos.api.review.dto.request.ReviewAddRequest;
import com.cocos.cocos.api.review.dto.response.MemberHospitalReviewListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewImageDeleteListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionListResponse;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
import com.cocos.cocos.validation.review.ReviewIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Review Controller", description = "리뷰 관련 API")
public interface ReviewControllerSwagger {

    @Operation(summary = "리뷰 작성 API", description = "병원 리뷰를 작성하는 API입니다. ")
    @ApiResponse(
            responseCode = "201",
            description = "요청에 성공했습니다."
    )
    @Parameter(name = "hospitalId", description = "병원 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<ReviewAddResponse>> addReview(
            @PathVariable(name = "hospitalId") @HospitalIdConstraint final Long hospitalId,
            @RequestBody final ReviewAddRequest reviewAddRequest
    );

    @Operation(summary = "리뷰 요약 조회 API", description = "병원 리뷰 요약 리스트를 조회하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    @Parameter(name = "hospitalId", description = "병원 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<ReviewSummaryListResponse>> getReviewSummaryList(
            @PathVariable(name = "hospitalId") @HospitalIdConstraint final Long hospitalId
    );

    @Operation(summary = "리뷰 요약 옵션 리스트 조회 API", description = "병원 리뷰 요약 옵션 리스트를 조회하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    public ResponseEntity<BaseResponse<ReviewSummaryOptionListResponse>> getReviewSummaryOptionList();

    @Operation(summary = "리뷰 삭제 API", description = "리뷰를 삭제하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    @Parameter(name = "reviewId", description = "리뷰 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<ReviewImageDeleteListResponse>> deleteReview(
            @PathVariable(name = "reviewId") @ReviewIdConstraint final Long reviewId
    );

    @Operation(summary = "사용자 리뷰 리스트 조회 API", description = "마이페이지 리뷰 리스트 조회 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    @Parameter(name = "nickname", description = "사용자 닉네임", required = false)
    @Parameter(name = "cursorId", description = "페이징용 마지막 리뷰 ID", required = false)
    @Parameter(name = "size", description = "페이지당 조회할 리뷰 개수 (1~20) 비로그인 시 최대 4개")
    public ResponseEntity<BaseResponse<MemberHospitalReviewListResponse>> getMemberHospitalReviewList(
            @RequestParam(name = "nickname", required = false) final String nickname,
            @RequestParam(name = "cursorId", required = false) final Long cursorId,
            @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) @Max(value = 20) final int size
    );
}
