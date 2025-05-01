package com.cocos.cocos.api.review.service;

import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.animal.entity.Animal;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.api.review.dto.response.*;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.review.db.*;
import com.cocos.cocos.db.review.repository.*;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.enums.pet.Gender;
import com.cocos.cocos.external.MemberDataS3Client;
import com.cocos.cocos.util.SortConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewSummaryRepository reviewSummaryRepository;
    private final ReviewSymptomRepository reviewSymptomRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewSummaryOptionRepository reviewSummaryOptionRepository;
    private final MemberDataS3Client memberDataS3Client;
    private final HospitalRepository hospitalRepository;
    private final BreedRepository breedRepository;
    private final AnimalRepository animalRepository;
    private final DiseaseRepository diseaseRepository;
    private final SymptomRepository symptomRepository;
    private final MemberRepository memberRepository;

    private static final String REVIEW_IMAGE_S3_PREFIX = "reviewImage";
    private static final boolean IS_GOOD_REVIEW = true;
    private static final int DEFAULT_PAGE_SIZE = 4;

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
                getReviewSummaryAndCount(reviewSummaryOptions, reviewIds, IS_GOOD_REVIEW),
                getReviewSummaryAndCount(reviewSummaryOptions, reviewIds, !IS_GOOD_REVIEW)
        );
    }

    @Transactional(readOnly = true)
    public MemberHospitalReviewListResponse getMemberHospitalReviewList(final String nickname, final Long cursorId, final int size, final Long memberId) {
        final Member member = findMember(nickname, memberId);

        final int searchSize = memberId == null ? DEFAULT_PAGE_SIZE : size;
        final Pageable pageable = PageRequest.of(0, searchSize, Sort.by(
                SortConstants.ID_DESC
        ));

        final List<Review> reviews = (cursorId == null)
                ? reviewRepository.findAllByMemberId(member.getId(), pageable)
                : reviewRepository.findAllByMemberIdAndIdLessThan(member.getId(), cursorId, pageable);

        final List<Long> reviewIds = reviews.stream().map(Review::getId).toList();

        final Set<Long> hospitalIds = reviews.stream().map(Review::getHospitalId).collect(Collectors.toSet());
        final Map<Long, Hospital> hospitalMap = hospitalRepository.findAllById(hospitalIds).stream()
                .collect(Collectors.toMap(Hospital::getId, Function.identity()));

        final List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewIdIn(reviewIds);
        final Map<Long, List<String>> reviewIdToImageUrls = reviewImages.stream()
                .collect(Collectors.groupingBy(
                        ReviewImage::getReviewId,
                        Collectors.mapping(img -> memberDataS3Client.getPresignedUrl(img.getImage()), Collectors.toList())
                ));

        final Set<Long> breedIds = reviews.stream().map(Review::getBreedId).collect(Collectors.toSet());
        final Map<Long, Breed> breedMap = breedRepository.findAllById(breedIds).stream()
                .collect(Collectors.toMap(Breed::getId, Function.identity()));
        final Set<Long> animalIds = breedMap.values().stream()
                .map(Breed::getAnimalId)
                .collect(Collectors.toSet());
        final Map<Long, Animal> animalMap = animalRepository.findAllById(animalIds).stream()
                .collect(Collectors.toMap(Animal::getId, Function.identity()));

        final Set<Long> diseaseIds = reviews.stream().map(Review::getDiseaseId).collect(Collectors.toSet());
        final Map<Long, Disease> diseaseMap = diseaseRepository.findAllById(diseaseIds).stream()
                .collect(Collectors.toMap(Disease::getId, Function.identity()));

        final List<ReviewSymptom> reviewSymptoms = reviewSymptomRepository.findByReviewIdIn(reviewIds);
        final Map<Long, List<Long>> reviewIdToSymptomIds = reviewSymptoms.stream()
                .collect(Collectors.groupingBy(
                        ReviewSymptom::getReviewId,
                        Collectors.mapping(ReviewSymptom::getSymptomId, Collectors.toList())
                ));
        final Set<Long> symptomIds = reviewSymptoms.stream().map(ReviewSymptom::getSymptomId).collect(Collectors.toSet());
        final Map<Long, Symptom> symptomMap = symptomRepository.findAllById(symptomIds).stream()
                .collect(Collectors.toMap(Symptom::getId, Function.identity()));

        final List<ReviewSummary> reviewSummaries = reviewSummaryRepository.findByReviewIdIn(reviewIds);
        final Map<Long, List<Long>> reviewIdToSummaryIds = reviewSummaries.stream()
                .collect(Collectors.groupingBy(
                        ReviewSummary::getReviewId,
                        Collectors.mapping(ReviewSummary::getReviewSummaryOptionId, Collectors.toList())
                ));
        final Set<Long> reviewSummaryOptionIds = reviewSummaries.stream().map(ReviewSummary::getReviewSummaryOptionId).collect(Collectors.toSet());
        final Map<Long, ReviewSummaryOption> reviewSummaryOptionMap = reviewSummaryOptionRepository.findAllById(reviewSummaryOptionIds).stream()
                .collect(Collectors.toMap(ReviewSummaryOption::getId, Function.identity()));

        final List<HospitalReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    final Hospital hospital = requireEntity(hospitalMap.get(review.getHospitalId()), FailMessage.NOT_FOUND_HOSPITAL);
                    final Breed breed = requireEntity(breedMap.get(review.getBreedId()), FailMessage.NOT_FOUND_BREED);
                    final Animal animal = requireEntity(animalMap.get(breed.getAnimalId()), FailMessage.NOT_FOUND_ANIMAL);
                    final Disease disease = requireEntity(diseaseMap.get(review.getDiseaseId()), FailMessage.NOT_FOUND_DISEASE);

                    final List<String> imageUrls = reviewIdToImageUrls.getOrDefault(review.getId(), List.of());

                    final List<String> symptomResponses = reviewIdToSymptomIds.getOrDefault(review.getId(), List.of()).stream()
                            .map(symptomId -> {
                                final Symptom symptom = requireEntity(symptomMap.get(symptomId), FailMessage.NOT_FOUND_SYMPTOM);
                                return symptom.getName();
                            })
                            .toList();

                    final List<Long> summaryOptionIds = reviewIdToSummaryIds.getOrDefault(review.getId(), List.of());
                    final List<ReviewSummaryOption> summaryOptions = summaryOptionIds.stream()
                            .map(optionId -> requireEntity(reviewSummaryOptionMap.get(optionId), FailMessage.NOT_FOUND_SUMMARY_OPTION))
                            .toList();

                    final List<ReviewSummaryOptionResponse> goodReviewSummaries = summaryOptions.stream()
                            .filter(opt -> Boolean.TRUE.equals(opt.getIsGood()))
                            .map(opt -> ReviewSummaryOptionResponse.of(opt.getId(), opt.getLabel()))
                            .toList();

                    final List<ReviewSummaryOptionResponse> badReviewSummaries = summaryOptions.stream()
                            .filter(opt -> Boolean.FALSE.equals(opt.getIsGood()))
                            .map(opt -> ReviewSummaryOptionResponse.of(opt.getId(), opt.getLabel()))
                            .toList();

                    final ReviewSummaryOptionListResponse summaryOptionList = ReviewSummaryOptionListResponse.of(
                            goodReviewSummaries, badReviewSummaries
                    );


                    return HospitalReviewResponse.of(
                            review.getId(),
                            hospital.getId(),
                            hospital.getName(),
                            review.getVisitedAt(),
                            hospital.getDisplayAddress(),
                            review.getContent(),
                            summaryOptionList,
                            imageUrls,
                            symptomResponses,
                            disease.getName(),
                            animal.getName(),
                            review.getGender(),
                            breed.getName(),
                            review.getWeight()
                    );
                })
                .toList();

        return MemberHospitalReviewListResponse.of(
                reviews.isEmpty() ? null : reviews.getLast().getId(),
                reviewResponses
        );
    }

    private <T> T requireEntity(final T entity, final FailMessage message) {
        if (entity == null) throw new CocosException(message);
        return entity;
    }

    private Member findMember(final String nickname, final Long memberId) {
        if (memberId == null && nickname == null) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_MEMBER_QUERY);
        }

        if (nickname != null) {
            return memberRepository.findByNickname(nickname).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
        }
        return memberRepository.findById(memberId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
    }

    private List<ReviewSummaryResponse> getReviewSummaryAndCount(final List<ReviewSummaryOption> reviewSummaryOptions, final List<Long> reviewIds, final boolean isGood) {
        return reviewSummaryOptions.stream()
                .filter(reviewSummaryOption -> reviewSummaryOption.getIsGood() == isGood)
                .map(reviewSummaryOption -> ReviewSummaryResponse.of(
                        reviewSummaryOption.getId(),
                        reviewSummaryOption.getLabel(),
                        reviewSummaryRepository.countByReviewIdInAndReviewSummaryOptionId(reviewIds, reviewSummaryOption.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewSummaryOptionListResponse getReviewSummaryOptionList() {
        final List<ReviewSummaryOption> reviewSummaryOptions = reviewSummaryOptionRepository.findAll();
        return ReviewSummaryOptionListResponse.of(
                getReviewSummaryOptionList(reviewSummaryOptions, IS_GOOD_REVIEW),
                getReviewSummaryOptionList(reviewSummaryOptions, !IS_GOOD_REVIEW)
        );
    }

    private List<ReviewSummaryOptionResponse> getReviewSummaryOptionList(final List<ReviewSummaryOption> reviewSummaryOptions, final boolean isGood) {
        return reviewSummaryOptions.stream()
                .filter(reviewSummaryOption -> reviewSummaryOption.getIsGood() == isGood)
                .map(reviewSummaryOption -> ReviewSummaryOptionResponse.of(
                        reviewSummaryOption.getId(),
                        reviewSummaryOption.getLabel())
                )
                .toList();
    }
}
