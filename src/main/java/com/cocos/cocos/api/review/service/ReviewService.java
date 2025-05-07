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
import com.cocos.cocos.db.district.entity.District;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.api.review.dto.response.*;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.review.db.*;
import com.cocos.cocos.db.review.repository.*;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.location.LocationType;
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
    private final PetRepository petRepository;
    private final DistrictRepository districtRepository;

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

        // TODO: 팀 컨벤션 측면에서, Map방식과 filter 방식 중 적절한 방식 논의 필요
        final Map<Long, Hospital> hospitalMap = getHospitalMap(reviews);
        final Map<Long, List<String>> reviewIdToImageUrls = getReviewIdToImageUrls(reviewIds);
        final Map<Long, Breed> breedMap = getBreedMapFromReviews(reviews);
        final Map<Long, Animal> animalMap = getAnimalMap(breedMap);
        final Map<Long, Disease> diseaseMap = getDiseaseMap(reviews);
        final Map<Long, List<String>> symptomMap = getReviewIdToSymptoms(reviewIds);
        final Map<Long, List<ReviewSummaryOption>> reviewSummaryOptionsMap = getReviewSummaryOptionsMap(reviewIds);

        final List<MemberHospitalReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    final Hospital hospital = hospitalMap.get(review.getHospitalId());
                    final Breed breed = breedMap.get(review.getBreedId());
                    final Animal animal = animalMap.get(breed.getAnimalId());
                    final Disease disease = diseaseMap.get(review.getDiseaseId());
                    final List<String> imageUrls = reviewIdToImageUrls.getOrDefault(review.getId(), List.of());
                    final List<String> symptoms = symptomMap.getOrDefault(review.getId(), List.of());

                    final List<ReviewSummaryOption> summaryOptions = reviewSummaryOptionsMap.getOrDefault(review.getId(), List.of());

                    final List<ReviewSummaryOptionResponse> goodReviewSummaries = getReviewSummaryOptionList(summaryOptions, IS_GOOD_REVIEW);
                    final List<ReviewSummaryOptionResponse> badReviewSummaries = getReviewSummaryOptionList(summaryOptions, !IS_GOOD_REVIEW);
                    final ReviewSummaryOptionListResponse summaryOptionList = ReviewSummaryOptionListResponse.of(
                            goodReviewSummaries, badReviewSummaries
                    );

                    return MemberHospitalReviewResponse.of(
                            review.getId(),
                            hospital.getId(),
                            hospital.getName(),
                            review.getVisitedAt(),
                            hospital.getDisplayAddress(),
                            review.getContent(),
                            summaryOptionList,
                            imageUrls,
                            symptoms,
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

    @Transactional(readOnly = true)
    public HospitalReviewListResponse getHospitalReviewList(final Long hospitalId, final Long summaryOptionId, final Long cursorId, final int size, final Long bodyId, final Long locationId, final LocationType locationType, final Long memberId) {
        final int searchSize = memberId == null ? DEFAULT_PAGE_SIZE : size;
        final Pageable pageable = PageRequest.of(0, searchSize, Sort.by(
                SortConstants.ID_DESC
        ));

        final List<Review> reviews = getReviews(locationId, locationType, hospitalId, summaryOptionId, cursorId, bodyId, pageable);

        final Map<Long, Hospital> hospitalMap = getHospitalMap(reviews);
        final List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        final Set<Long> memberIds = reviews.stream().map(Review::getMemberId).collect(Collectors.toSet());

        final Map<Long, List<String>> reviewIdToImageUrls = getReviewIdToImageUrls(reviewIds);
        final Map<Long, Member> memberMap = getMemberMap(memberIds);
        final Map<Long, Pet> petMap = getPetMap(memberIds);
        final Map<Long, Breed> breedMap = getBreedMapFromReviewsAndPets(reviews, petMap);
        final Map<Long, Animal> animalMap = getAnimalMap(breedMap);
        final Map<Long, Disease> diseaseMap = getDiseaseMap(reviews);
        final Map<Long, List<String>> symptomMap = getReviewIdToSymptoms(reviewIds);
        final Map<Long, List<ReviewSummaryOption>> reviewSummaryOptionsMap = getReviewSummaryOptionsMap(reviewIds);

        final Integer reviewCount = getReviewCountByHospitalId(hospitalId);

        final List<HospitalReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    final Hospital hospital = hospitalMap.get(review.getHospitalId());
                    final Breed breed = breedMap.get(review.getBreedId());
                    final Member member = memberMap.get(review.getMemberId());
                    final Pet pet = petMap.get(review.getMemberId());
                    final Breed memberBreed = breedMap.get(pet.getBreedId());
                    final Animal animal = animalMap.get(breed.getAnimalId());
                    final Disease disease = diseaseMap.get(review.getDiseaseId());
                    final List<String> imageUrls = reviewIdToImageUrls.getOrDefault(review.getId(), List.of());
                    final List<String> symptoms = symptomMap.getOrDefault(review.getId(), List.of());

                    final List<ReviewSummaryOption> summaryOptions = reviewSummaryOptionsMap.getOrDefault(review.getId(), List.of());

                    final List<ReviewSummaryOptionResponse> goodReviewSummaries = getReviewSummaryOptionList(summaryOptions, IS_GOOD_REVIEW);
                    final List<ReviewSummaryOptionResponse> badReviewSummaries = getReviewSummaryOptionList(summaryOptions, !IS_GOOD_REVIEW);
                    final ReviewSummaryOptionListResponse summaryOptionList = ReviewSummaryOptionListResponse.of(
                            goodReviewSummaries, badReviewSummaries
                    );

                    return HospitalReviewResponse.of(
                            review.getId(),
                            member.getId(),
                            member.getNickname(),
                            memberBreed.getName(),
                            pet.getAge(),
                            hospital.getId(),
                            hospital.getName(),
                            review.getVisitedAt(),
                            hospital.getDisplayAddress(),
                            review.getContent(),
                            summaryOptionList,
                            imageUrls,
                            symptoms,
                            disease.getName(),
                            animal.getName(),
                            review.getGender(),
                            breed.getName(),
                            review.getWeight()
                    );
                })
                .toList();

        return HospitalReviewListResponse.of(
                reviewCount,
                reviews.isEmpty() ? null : reviews.getLast().getId(),
                reviewResponses
        );
    }

    private List<Review> getReviews(final Long locationId, final LocationType locationType, final Long hospitalId, final Long summaryOptionId, final Long cursorId, final Long bodyId, final Pageable pageable) {

        if (bodyId != null && locationId != null && locationType != null) {
            final List<Long> symptomIds = symptomRepository.findAllByBodyId(bodyId).stream()
                    .map(Symptom::getId)
                    .toList();

            final List<Long> districtIds = getDistrictIds(locationId, locationType);
            final List<Long> hospitalIds = hospitalRepository.findAllByDistrictIdIn(districtIds).stream()
                    .map(Hospital::getId)
                    .toList();

            final List<Long> reviewIds = reviewSymptomRepository.findBySymptomIdIn(symptomIds).stream()
                    .map(ReviewSymptom::getReviewId)
                    .toList();

            return reviewRepository.findByBodyAndLocationAndSummaryOptionIdAndCursorId(reviewIds, hospitalIds, summaryOptionId, cursorId, pageable);
        } else if (hospitalId != null) {
            return reviewRepository.findByHospitalIdAndSummaryOptionIdAndCursorId(hospitalId, summaryOptionId, cursorId, pageable);
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_MISSING_BODY);
        }
    }

    private Integer getReviewCountByHospitalId(final Long hospitalId) {
        if (hospitalId == null) {
            return null;
        } else {
            return hospitalRepository.findById(hospitalId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_HOSPITAL)).getReviewCount();        }
    }


    private Map<Long, Member> getMemberMap(final Set<Long> memberIds) {
        final Map<Long, Member> membermap = memberRepository.findAllById(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        if (membermap.size() != memberIds.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_MEMBER);
        }
        return membermap;
    }

    private Map<Long, Pet> getPetMap(final Set<Long> memberIds) {
        final Map<Long, Pet> petMap = petRepository.findAllByMemberIdIn(memberIds).stream()
                .collect(Collectors.toMap(Pet::getMemberId, Function.identity()));

        if (memberIds.size() != petMap.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_PET);
        }
        return petMap;
    }

    private Map<Long, Disease> getDiseaseMap(final List<Review> reviews) {
        final Set<Long> diseaseIds = reviews.stream().map(Review::getDiseaseId).collect(Collectors.toSet());
        final Map<Long, Disease> diseaseMap = diseaseRepository.findAllById(diseaseIds).stream()
                .collect(Collectors.toMap(Disease::getId, Function.identity()));

        if (diseaseMap.size() != diseaseIds.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_DISEASE);
        }
        return diseaseMap;
    }

    private Map<Long, Animal> getAnimalMap(final Map<Long, Breed> breedMap) {
        final Set<Long> animalIds = breedMap.values().stream()
                .map(Breed::getAnimalId)
                .collect(Collectors.toSet());
        final Map<Long, Animal> animalMap = animalRepository.findAllById(animalIds).stream()
                .collect(Collectors.toMap(Animal::getId, Function.identity()));

        if (animalMap.size() != animalIds.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_ANIMAL);
        }
        return animalMap;
    }

    private Map<Long, Breed> getBreedMapFromReviewsAndPets(final List<Review> reviews, final Map<Long, Pet> pets) {
        final Set<Long> breedIdsFromReviews = reviews.stream()
                .map(Review::getBreedId)
                .collect(Collectors.toSet());

        final Set<Long> breedIdsFromPets = pets.values().stream()
                .map(Pet::getBreedId)
                .collect(Collectors.toSet());

        final Set<Long> allBreedIds = new HashSet<>();
        allBreedIds.addAll(breedIdsFromReviews);
        allBreedIds.addAll(breedIdsFromPets);

        return fetchBreedMap(allBreedIds);
    }

    private Map<Long, Breed> getBreedMapFromReviews(final List<Review> reviews) {
        final Set<Long> breedIds = reviews.stream()
                .map(Review::getBreedId)
                .collect(Collectors.toSet());
        return fetchBreedMap(breedIds);
    }

    private Map<Long, Breed> fetchBreedMap(final Set<Long> breedIds) {
        final Map<Long, Breed> breedMap = breedRepository.findAllById(breedIds).stream()
                .collect(Collectors.toMap(Breed::getId, Function.identity()));

        if (breedMap.size() != breedIds.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_BREED);
        }
        return breedMap;
    }

    private Map<Long, Hospital> getHospitalMap(final List<Review> reviews) {
        final Set<Long> hospitalIds = reviews.stream().map(Review::getHospitalId).collect(Collectors.toSet());
        final Map<Long, Hospital> hospitalMap = hospitalRepository.findAllById(hospitalIds).stream()
                .collect(Collectors.toMap(Hospital::getId, Function.identity()));

        if (hospitalMap.size() != hospitalIds.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_HOSPITAL);
        }
        return hospitalMap;
    }

    private Map<Long, List<String>> getReviewIdToImageUrls(final List<Long> reviewIds) {
        final List<ReviewImage> reviewImages = reviewImageRepository.findAllByReviewIdIn(reviewIds);
        return reviewImages.stream()
                .collect(Collectors.groupingBy(
                        ReviewImage::getReviewId,
                        Collectors.mapping(img -> memberDataS3Client.getPresignedUrl(img.getImage()), Collectors.toList())
                ));
    }

    private Map<Long, List<String>> getReviewIdToSymptoms(final List<Long> reviewIds) {
        final List<ReviewSymptom> reviewSymptoms = reviewSymptomRepository.findByReviewIdIn(reviewIds);
        final Set<Long> symptomIds = reviewSymptoms.stream()
                .map(ReviewSymptom::getSymptomId)
                .collect(Collectors.toSet());
        final Map<Long, Symptom> symptomMap = symptomRepository.findAllById(symptomIds).stream()
                .collect(Collectors.toMap(Symptom::getId, Function.identity()));

        return reviewSymptoms.stream()
                .collect(Collectors.groupingBy(
                        ReviewSymptom::getReviewId,
                        Collectors.mapping(
                                reviewSymptom -> {
                                    final Symptom symptom = symptomMap.get(reviewSymptom.getSymptomId());
                                    if (symptom == null) {
                                        throw new CocosException(FailMessage.NOT_FOUND_SYMPTOM);
                                    }
                                    return symptom.getName();
                                },
                                Collectors.toList()
                        )
                ));
    }

    private Map<Long, List<ReviewSummaryOption>> getReviewSummaryOptionsMap(final List<Long> reviewIds) {

        final List<ReviewSummary> reviewSummaries = reviewSummaryRepository.findByReviewIdIn(reviewIds);
        final Set<Long> reviewSummaryOptionIds = reviewSummaries.stream().map(ReviewSummary::getReviewSummaryOptionId).collect(Collectors.toSet());

        final Map<Long, ReviewSummaryOption> reviewSummaryOptionMap = reviewSummaryOptionRepository.findAllById(reviewSummaryOptionIds).stream()
                .collect(Collectors.toMap(ReviewSummaryOption::getId, Function.identity()));

        return reviewSummaries.stream()
                .collect(Collectors.groupingBy(
                        ReviewSummary::getReviewId,
                        Collectors.mapping(
                                reviewSummary -> {
                                    final ReviewSummaryOption reviewSummaryOption = reviewSummaryOptionMap.get(reviewSummary.getReviewSummaryOptionId());
                                    if (reviewSummaryOption == null) {
                                        throw new CocosException(FailMessage.NOT_FOUND_SUMMARY_OPTION);
                                    }
                                    return reviewSummaryOption;
                                },
                                Collectors.toList()
                        )
                ));
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

    @Transactional
    public ReviewImageDeleteListResponse deleteReview(final Long memberId, final Long reviewId) {
        if (memberId == null) {
            throw new CocosException(FailMessage.UNAUTHORIZED);
        }

        final Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_REVIEW)
        );

        if (!review.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.UNAUTHORIZED_NOT_WRITER);
        }

        final List<String> reviewImages = reviewImageRepository.findAllByReviewId(reviewId).stream()
                .map(reviewImage -> memberDataS3Client.deletePresignedUrl(reviewImage.getImage()))
                .toList();

        reviewSummaryRepository.deleteAllByReviewId(reviewId);
        reviewSymptomRepository.deleteAllByReviewId(reviewId);
        reviewImageRepository.deleteAllByReviewId(reviewId);

        final Hospital hospital = hospitalRepository.findById(review.getHospitalId()).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_HOSPITAL)
        );

        hospital.deleteReview();
        reviewRepository.deleteById(reviewId);

        return ReviewImageDeleteListResponse.of(reviewImages);
    }

    // TODO: hospitalService와 중복. 공용으로 관리하도록 리팩 필요
    private List<Long> getDistrictIds(final Long locationId, final LocationType locationType) {
        if (locationType == LocationType.CITY) {
            return districtRepository.findAllByCityId(locationId).stream().map(District::getId).toList();
        }
        if (locationType == LocationType.DISTRICT) {
            return List.of(locationId);
        }
        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_LOCATION_TYPE);
    }
}
