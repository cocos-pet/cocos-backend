package com.cocos.cocos.api.hospital.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record HospitalVisitPurposeResponse(
        @Schema(description = "병원 방문 목적 아이디", example = "1")
        Long id,
        @Schema(description = "병원 방문 목적", example = "진료")
        String label
) {
    public static HospitalVisitPurposeResponse of(final Long id, final String label) {
        return new HospitalVisitPurposeResponse(id, label);
    }
}
