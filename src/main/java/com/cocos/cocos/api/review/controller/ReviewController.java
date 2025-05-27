package com.cocos.cocos.api.review.controller;

import com.cocos.cocos.api.review.dto.request.ReviewAddRequest;
import com.cocos.cocos.api.review.dto.response.MemberHospitalReviewListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewImageDeleteListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionListResponse;
import com.cocos.cocos.api.review.dto.request.ReviewListRequest;
import com.cocos.cocos.api.review.dto.response.*;
import com.cocos.cocos.api.review.service.ReviewService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.util.validation.EntityExistsValidator;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import com.cocos.cocos.validation.review.ReviewIdConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
@Validated
public class ReviewController implements ReviewControllerSwagger {

    private final ReviewService reviewService;
    private final EntityExistsValidator entityExistsValidator;

    @PostMapping("/hospitals/{hospitalId}/reviews")
    public ResponseEntity<BaseResponse<ReviewAddResponse>> addReview(
            @PathVariable(name = "hospitalId") @HospitalIdConstraint final Long hospitalId,
            @RequestBody @Valid final ReviewAddRequest reviewAddRequest
    ) {
        entityExistsValidator.validatePetByMemberId(PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.CREATED, reviewService.addReview(
                PrincipalHandler.getMemberIdFromPrincipal(), hospitalId, reviewAddRequest.breedId(), reviewAddRequest.gender(),
                reviewAddRequest.weight(), reviewAddRequest.visitedAt(), reviewAddRequest.content(),
                reviewAddRequest.purposeId(), reviewAddRequest.diseaseId(), reviewAddRequest.symptomIds(),
                reviewAddRequest.goodReviewIds(), reviewAddRequest.badReviewIds(), reviewAddRequest.images()
        ));
    }

    @GetMapping("/hospitals/{hospitalId}/reviews/summary")
    public ResponseEntity<BaseResponse<ReviewSummaryListResponse>> getReviewSummaryList(
            @PathVariable(name = "hospitalId") @HospitalIdConstraint final Long hospitalId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, reviewService.getReviewSummaryList(hospitalId));
    }

    @GetMapping("/hospitals/reviews/summary/option")
    public ResponseEntity<BaseResponse<ReviewSummaryOptionListResponse>> getReviewSummaryOptionList() {
        return SuccessResponse.success(SuccessMessage.OK, reviewService.getReviewSummaryOptionList());
    }

    @GetMapping("/hospitals/reviews/members")
    public ResponseEntity<BaseResponse<MemberHospitalReviewListResponse>> getMemberHospitalReviewList(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname,
            @RequestParam(name = "cursorId", required = false) @ReviewIdConstraint final Long cursorId,
            @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) @Max(value = 20) final int size
    ) {
        return SuccessResponse.success(SuccessMessage.OK, reviewService.getMemberHospitalReviewList(nickname, cursorId, size, PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @PostMapping("/hospitals/reviews/filter")
    public ResponseEntity<BaseResponse<HospitalReviewListResponse>> getHospitalReviewList(
            @RequestBody @Valid final ReviewListRequest reviewListRequest
    ) {
        return SuccessResponse.success(SuccessMessage.OK, reviewService.getHospitalReviewList(reviewListRequest.hospitalId(), reviewListRequest.summaryOptionId(), reviewListRequest.cursorId(), reviewListRequest.size(), reviewListRequest.bodyId(), reviewListRequest.locationId(), reviewListRequest.locationType(), PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @DeleteMapping("/hospitals/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<ReviewImageDeleteListResponse>> deleteReview(
            @PathVariable(name = "reviewId") @ReviewIdConstraint final Long reviewId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, reviewService.deleteReview(PrincipalHandler.getMemberIdFromPrincipal(), reviewId));
    }
}
