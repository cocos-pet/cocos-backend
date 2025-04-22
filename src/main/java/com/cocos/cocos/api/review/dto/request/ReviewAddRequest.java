package com.cocos.cocos.api.review.dto.request;

import com.cocos.cocos.enums.pet.Gender;
import lombok.Builder;

import java.util.List;

public record ReviewAddRequest(
        Long breedId,
        Gender gender,
        Integer weight,
        String visitedAt,
        String content,
        Long purposeId,
        Long diseaseId,
        List<Long> symptomIds,
        List<Long> goodReviewIds,
        List<Long> badReviewIds,
        List<String> images
) {
}
