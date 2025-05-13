package com.cocos.cocos.api.review.dto.request;

import com.cocos.cocos.enums.location.LocationType;
import com.cocos.cocos.validation.body.BodyIdConstraint;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
import com.cocos.cocos.validation.review.ReviewIdConstraint;
import com.cocos.cocos.validation.review.SummaryOptionIdConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewListRequest(
        @Schema(description = "리뷰 요약 아이디", example = "1", nullable = true)
        @SummaryOptionIdConstraint
        Long summaryOptionId,
        @Schema(description = "커서 아이디 (마지막으로 전달받은 리뷰 아이디", example = "1", nullable = true)
        @ReviewIdConstraint
        Long cursorId,
        @Schema(description = "병원 아이디", nullable = true, example = "1")
        @HospitalIdConstraint
        Long hospitalId,
        @Schema(description = "신체 아이디", nullable = true, example = "1")
        @BodyIdConstraint
        Long bodyId,
        @Schema(description = "지역 아이디", nullable = true, example = "1")
        Long locationId,  // TODO: 유효성 검증 로직 추가
        @Schema(description = "지역 타입", nullable = true, example = "DISTRICT")
        LocationType locationType,
        @Schema(description = "페이지네이션 크기 ", example = "10", defaultValue = "10")
        @Min(value = 1, message = "size는 1이상입니다.")
        @Max(value = 20, message = "size는 20이하입니다.")
        int size
) {
    public ReviewListRequest(final Long summaryReviewId, final Long cursorId, final Long hospitalId, final Long bodyId, final Long locationId, final LocationType locationType) {
        this(summaryReviewId, cursorId, hospitalId, bodyId, locationId, locationType, 10);
    }

}
