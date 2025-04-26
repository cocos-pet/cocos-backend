package com.cocos.cocos.api.hospital.dto.response;

import java.util.List;

public record HospitalVisitPurposeListResponse(
        List<HospitalVisitPurposeResponse> purposes
) {
    public static HospitalVisitPurposeListResponse of(final List<HospitalVisitPurposeResponse> purposes) {
        return new HospitalVisitPurposeListResponse(purposes);
    }
}
