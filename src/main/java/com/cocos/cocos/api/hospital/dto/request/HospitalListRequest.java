package com.cocos.cocos.api.hospital.dto.request;

import com.cocos.cocos.enums.hospital.HospitalSortCriteria;
import com.cocos.cocos.enums.location.LocationType;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
import com.cocos.cocos.validation.location.HasLocation;
import com.cocos.cocos.validation.location.LocationConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
@LocationConstraint
public record HospitalListRequest(

        @Schema(description = "지역 타입",nullable = true, example = "CITY")
        LocationType locationType,

        @Schema(description = "지역 아이디", nullable = true, example = "1")
        Long locationId,

        @Schema(description = "마지막으로 조회된 병원 아이디 (첫 요청을 제외하고 필수로 보내야 합니다.)", nullable = true, example = "1")
        @HospitalIdConstraint
        Long cursorId,

        @Schema(description = "마지막으로 조회된 병원 리뷰수 (정렬 기준이 REVIEW일 때는 첫 요청을 제외하고 필수로 보내야 합니다.)", nullable = true, example = "1")
        Integer cursorReviewCount,

        @Min(value = 1, message = "size는 1이상입니다.")
        @Max(value = 20, message = "size는 20이하입니다.")
        @Schema(description = "병원 요청 수 ", example = "10")
        int size,

        @Schema(description = "검색어 ", nullable = true, example = "병원")
        String keyword,

        @Schema(description = "정렬 기준", nullable = true, example = "REVIEW")
        HospitalSortCriteria sortBy
) implements HasLocation {
}
