package com.cocos.cocos.api.review.controller;

import com.cocos.cocos.api.review.dto.request.ReviewAddRequest;
import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewImageDeleteListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionListResponse;
import com.cocos.cocos.api.review.service.ReviewService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
import com.cocos.cocos.validation.review.ReviewIdConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class ReviewController implements ReviewControllerSwagger {

    private final ReviewService reviewService;

    @PostMapping("/hospitals/{hospitalId}/reviews")
    public ResponseEntity<BaseResponse<ReviewAddResponse>> addReview(
            @PathVariable(name = "hospitalId") @HospitalIdConstraint final Long hospitalId,
            @RequestBody final ReviewAddRequest reviewAddRequest
    ) {
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

    @DeleteMapping("/hospitals/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<ReviewImageDeleteListResponse>> deleteReview(
            @PathVariable(name = "reviewId") @ReviewIdConstraint final Long reviewId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, reviewService.deleteReview(PrincipalHandler.getMemberIdFromPrincipal(), reviewId));
    }
}
