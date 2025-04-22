package com.cocos.cocos.api.review.controller;

import com.cocos.cocos.api.review.dto.request.ReviewAddRequest;
import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.service.ReviewService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{hospitalId}")
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
}
