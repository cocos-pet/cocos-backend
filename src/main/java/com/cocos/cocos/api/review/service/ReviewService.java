package com.cocos.cocos.api.review.service;

import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryResponse;
import com.cocos.cocos.db.review.db.*;
import com.cocos.cocos.db.review.repository.*;
import com.cocos.cocos.enums.pet.Gender;
import com.cocos.cocos.external.MemberDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewSummaryRepository reviewSummaryRepository;
    private final ReviewSymptomRepository reviewSymptomRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewSummaryOptionRepository reviewSummaryOptionRepository;
    private final MemberDataS3Client memberDataS3Client;

    private static final String REVIEW_IMAGE_S3_PREFIX = "reviewImage";

    @Transactional
    public ReviewAddResponse addReview(final Long memberId, final Long hospitalId, final Long breedId, final Gender gender,
                                       final Integer weight, final String visitedAt, final String content,
                                       final Long purposeId, final Long diseaseId, final List<Long> symptomIds,
                                       final List<Long> goodReviewIds, final List<Long> badReviewIds, final List<String> images) {
        //ToDo: id 검증 로직 필요
        final Review review = reviewRepository.save(Review.builder()
                .breedId(breedId)
                .gender(gender)
                .weight(weight)
                .purposeId(purposeId)
                .content(content)
                .hospitalId(hospitalId)
                .memberId(memberId)
                .visitedAt(visitedAt)
                .diseaseId(diseaseId)
                .build());

        addReviewSummary(review.getId(), goodReviewIds);
        addReviewSummary(review.getId(), badReviewIds);

        // ToDo: 리뷰 요약 추가 로직과 비슷하기 때문에 이 부분 enum으로 구분하고 하나의 로직으로 합치는 것 고려
        if (symptomIds != null && !symptomIds.isEmpty()) {
            symptomIds.forEach(symptomId -> reviewSymptomRepository.save(
                    ReviewSymptom.builder()
                            .reviewId(review.getId())
                            .symptomId(symptomId)
                            .build()
            ));
        }

        if (images != null && !images.isEmpty()) {
            return ReviewAddResponse.of(images.stream()
                    .map(image -> {
                        // TODO: 파일 포맷, 이름 등을 ENUM에 모아두는 것도 좋아보임
                        final String fileName = String.format("%s/%s/%s", memberId, REVIEW_IMAGE_S3_PREFIX, UUID.randomUUID() + image);

                        reviewImageRepository.save(
                                ReviewImage.builder()
                                        .image(fileName)
                                        .reviewId(review.getId())
                                        .build()
                        );

                        return memberDataS3Client.putPresignedUrl(fileName);
                    })
                    .toList()
            );
        }

        return ReviewAddResponse.of(null);
    }

    private void addReviewSummary(final Long reviewId, final List<Long> reviewSummaryIds) {
        if (reviewSummaryIds != null && !reviewSummaryIds.isEmpty()) {
            reviewSummaryIds.forEach(reviewSummaryId -> reviewSummaryRepository.save(
                            ReviewSummary.builder()
                                    .reviewId(reviewId)
                                    .reviewSummaryOptionId(reviewSummaryId)
                                    .build()
                    )
            );
        }
    }

    @Transactional(readOnly = true)
    public ReviewSummaryListResponse getReviewSummaryList(final Long hospitalId) {
        final List<Long> reviewIds = reviewRepository.findAllByHospitalId(hospitalId).stream()
                .map(Review::getId)
                .toList();

        final List<ReviewSummaryOption> reviewSummaryOptions = reviewSummaryOptionRepository.findAll();

        return ReviewSummaryListResponse.of(
                reviewSummaryOptions.stream()
                        .filter(reviewSummaryOption -> reviewSummaryOption.getIsGood())
                        .map(reviewSummaryOption -> ReviewSummaryResponse.of(
                                reviewSummaryOption.getId(),
                                reviewSummaryOption.getLabel(),
                                reviewSummaryRepository.countByReviewIdInAndReviewSummaryOptionId(reviewIds, reviewSummaryOption.getId())
                        ))
                        .toList(),
                reviewSummaryOptions.stream()
                        .filter(reviewSummaryOption -> !reviewSummaryOption.getIsGood())
                        .map(reviewSummaryOption -> ReviewSummaryResponse.of(
                                reviewSummaryOption.getId(),
                                reviewSummaryOption.getLabel(),
                                reviewSummaryRepository.countByReviewIdInAndReviewSummaryOptionId(reviewIds, reviewSummaryOption.getId())
                        ))
                        .toList()
        );
    }
}
