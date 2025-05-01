package com.cocos.cocos.api.review.controller;

import com.cocos.cocos.api.review.dto.request.ReviewAddRequest;
import com.cocos.cocos.api.review.dto.response.MemberHospitalReviewListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionListResponse;
import com.cocos.cocos.api.review.service.ReviewService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
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

    @PostMapping("/hospitals/{hospitalId}/reviews")
    public ResponseEntity<BaseResponse<ReviewAddResponse>> addReview(
            @PathVariable(name = "hospitalId") final Long hospitalId,
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

    @GetMapping("/hospitals/reviews/members")
    public ResponseEntity<BaseResponse<MemberHospitalReviewListResponse>> getMemberHospitalReviewList(
            @RequestParam(name = "nickname", required = false) final String nickname,
            @RequestParam(name = "cursorId", required = false) final Long cursorId,
            @RequestParam(name = "size", defaultValue = "10") @Min(value = 1) @Max(value = 20) final int size
    ) {
        return SuccessResponse.success(SuccessMessage.OK, reviewService.getMemberHospitalReviewList(nickname, cursorId, size, PrincipalHandler.getMemberIdFromPrincipal()));
    }
}
