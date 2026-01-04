package com.cocos.cocos.api.review.dto.query;

import com.cocos.cocos.enums.location.LocationType;
import lombok.Builder;

@Builder
public record ReviewSearchCondition(
        Long locationId,
        LocationType locationType,
        Long hospitalId,
        Long summaryOptionId,
        Long cursorId,
        Long bodyId,
        Integer size
) {}