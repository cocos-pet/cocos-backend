package com.cocos.cocos.api.review.service;

import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.db.review.db.Review;
import com.cocos.cocos.db.review.repository.ReviewRepository;
import com.cocos.cocos.enums.pet.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewAddResponse of(final Long memberId, final Long hospitalId, final Long breedId, final Gender gender,
                                final Integer weight, final String visitedAt, final String content,
                                final Long purposeId, final Long diseaseId, final List<Long> symptomIds,
                                final List<Long> goodReviewIds, final List<Long> badReviewIds, final List<String> images) {
        final Review review = reviewRepository.save(Review.builder()
                .breedId(breedId)
                .gender(gender)
                .weight(weight)
                .purposeId(purposeId)
                .content(content)
                .hospitalId(hospitalId)
                .memberId(memberId)
                .visitedAt(visitedAt)
                .build());

        return null;
    }
}
