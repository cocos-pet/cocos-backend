package com.cocos.cocos.api.review.service;

import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.enums.pet.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    @Transactional
    public ReviewAddResponse of(final Long memberId, final Long breedId, final Gender gender,
                                final Integer weight, final String visitedAt, final String content,
                                final Long purposeId, final Long diseaseId, final List<Long> symptomIds,
                                final List<Long> goodReviewIds, final List<Long> badReviewIds, final List<String> images) {

        return null;
    }
}
