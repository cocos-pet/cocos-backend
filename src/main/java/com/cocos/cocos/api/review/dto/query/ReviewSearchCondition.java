package com.cocos.cocos.api.review.dto.query;

import com.cocos.cocos.enums.location.LocationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewSearchCondition {
    private Long locationId;
    private LocationType locationType;
    private Long hospitalId;
    private Long summaryOptionId;
    private Long cursorId;
    private Long bodyId;
    private Integer size;
}
