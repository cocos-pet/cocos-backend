package com.cocos.cocos.api.review.dto.request;

import com.cocos.cocos.enums.pet.Gender;
import lombok.Builder;

import java.util.List;

@Builder
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
    public static ReviewAddRequest of(final Long breedId, final Gender gender, final Integer weight,
                                      final String visitedAt, final String content, final Long purposeId, final Long diseaseId,
                                      final List<Long> symptomIds, final List<Long> goodReviewIds, final List<Long> badReviewIds, final List<String> images) {
        return ReviewAddRequest.builder()
                .breedId(breedId)
                .gender(gender)
                .weight(weight)
                .visitedAt(visitedAt)
                .content(content)
                .purposeId(purposeId)
                .diseaseId(diseaseId)
                .symptomIds(symptomIds)
                .goodReviewIds(goodReviewIds)
                .badReviewIds(badReviewIds)
                .images(images)
                .build();
    }
}
