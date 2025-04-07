package com.cocos.cocos.api.hospital.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record HospitalListResponse(
        @Schema(description = "리스트의 마지막 병원 아이디", example = "1")
        Long cursorId,
        @Schema(description = "리스트의 마지막 병원 리뷰수", example = "1")
        Integer cursorReviewCount,
        @Schema(description = "병원 리스트")
        List<HospitalResponse> hospitals
) {
    public static HospitalListResponse of(final Long cursorId, final Integer cursorReviewCount, final List<HospitalResponse> hospitals) {
        return new HospitalListResponse(cursorId, cursorReviewCount, hospitals);
    }
}
