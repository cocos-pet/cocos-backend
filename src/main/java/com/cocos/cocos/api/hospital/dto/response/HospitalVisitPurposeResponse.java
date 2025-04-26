package com.cocos.cocos.api.hospital.dto.response;

public record HospitalVisitPurposeResponse(
        Long id,
        String label
) {
    public static HospitalVisitPurposeResponse of(final Long id, final String label) {
        return new HospitalVisitPurposeResponse(id, label);
    }
}
