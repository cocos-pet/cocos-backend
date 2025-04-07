package com.cocos.cocos.enums.hospital;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HospitalSortCriteria {
    REVIEW("리뷰 많은 순", "reviewCount");

    private final String sortBy;
    private final String fieldName;
}
