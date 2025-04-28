package com.cocos.cocos.api.hospital.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record HospitalVisitPurposeListResponse(
        @Schema(description = "병원 방문 목적 리스트")
        List<HospitalVisitPurposeResponse> purposes
) {
    public static HospitalVisitPurposeListResponse of(final List<HospitalVisitPurposeResponse> purposes) {
        return new HospitalVisitPurposeListResponse(purposes);
    }
}
