package com.cocos.cocos.review;

import com.cocos.cocos.api.review.dto.query.ReviewSearchCondition;
import com.cocos.cocos.api.review.dto.response.HospitalReviewListResponse;
import com.cocos.cocos.api.review.dto.response.HospitalReviewResponse;
import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewImageDeleteListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryResponse;
import com.cocos.cocos.api.review.service.ReviewService;
import com.cocos.cocos.db.animal.entity.Animal;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.entity.VisitPurpose;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.db.hospital.repository.HospitalVisitPurposeRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.review.entity.Review;
import com.cocos.cocos.db.review.entity.ReviewImage;
import com.cocos.cocos.db.review.entity.ReviewSummary;
import com.cocos.cocos.db.review.entity.ReviewSummaryOption;
import com.cocos.cocos.db.review.entity.ReviewSymptom;
import com.cocos.cocos.db.review.repository.ReviewImageRepository;
import com.cocos.cocos.db.review.repository.ReviewRepository;
import com.cocos.cocos.db.review.repository.ReviewSummaryOptionRepository;
import com.cocos.cocos.db.review.repository.ReviewSummaryRepository;
import com.cocos.cocos.db.review.repository.ReviewSymptomRepository;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.location.LocationType;
import com.cocos.cocos.enums.pet.Gender;
import com.cocos.cocos.external.MemberDataS3Client;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("리뷰 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReviewServiceTest {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ReviewSummaryRepository reviewSummaryRepository;

    @Mock
    ReviewSymptomRepository reviewSymptomRepository;

    @Mock
    ReviewImageRepository reviewImageRepository;

    @Mock
    ReviewSummaryOptionRepository reviewSummaryOptionRepository;

    @Mock
    MemberDataS3Client memberDataS3Client;

    @Mock
    HospitalRepository hospitalRepository;

    @Mock
    BreedRepository breedRepository;

    @Mock
    AnimalRepository animalRepository;

    @Mock
    DiseaseRepository diseaseRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PetRepository petRepository;

    @Mock
    DistrictRepository districtRepository;

    @Mock
    HospitalVisitPurposeRepository hospitalVisitPurposeRepository;



    @Mock
    SymptomRepository symptomRepository;

    @Test
    @DisplayName("리뷰를 작성할 수 있다.")
    void addReview() {
        //given
        final Long memberId = 1L;
        final Long hospitalId = 2L;
        final Long breedId = 1L;
        final Gender gender = Gender.F;
        final double weight = 7.0;
        final LocalDateTime visitedAt = LocalDateTime.parse("2025-07-12T15:20:19");
        final String content = "내용";
        final Long purposeId = 2L;
        final Long diseaseId = 3L;
        final List<Long> symptomIds = new ArrayList<>(List.of(1L, 2L));
        final List<Long> goodReviewIds = new ArrayList<>(List.of(4L, 5L, 6L, 10L));
        final List<Long> badReviewIds = new ArrayList<>(List.of(7L, 8L, 9L));
        final String image1 = "image1";
        final String image2 = "image2";
        final List<String> images = new ArrayList<>(List.of(image1, image2));
        final String presignedUrl = "presignedUrl";
        final List<String> presignedUrls = new ArrayList<>(List.of(presignedUrl, presignedUrl));

        final Review review = Review.builder()
                .breedId(breedId)
                .gender(gender)
                .weight(weight)
                .purposeId(purposeId)
                .content(content)
                .hospitalId(hospitalId)
                .memberId(memberId)
                .visitedAt(visitedAt)
                .diseaseId(diseaseId)
                .build();

        final Hospital hospital = Hospital.builder()
                        .build();

        ReflectionTestUtils.setField(hospital, "id", hospitalId);
        ReflectionTestUtils.setField(review, "id", 123L);

        BDDMockito.given(memberDataS3Client.putPresignedUrl(any())).willReturn(presignedUrl);
        BDDMockito.given(reviewRepository.save(any(Review.class)))
                .willReturn(review);
        BDDMockito.given(hospitalRepository.findById(hospitalId)).willReturn(Optional.of(Hospital.builder().build()));

        final ReviewAddResponse expected = ReviewAddResponse.of(
                presignedUrls
        );

        //when
        final ReviewAddResponse actual = reviewService.addReview(memberId, hospitalId, breedId, gender, weight, visitedAt
                , content, purposeId, diseaseId, symptomIds, goodReviewIds, badReviewIds, images);

        //then
        Assertions.assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
        BDDMockito.verify(reviewRepository, times(1)).save(any(Review.class));
        BDDMockito.verify(reviewSummaryRepository, times(7)).save(any(ReviewSummary.class));
        BDDMockito.verify(reviewSymptomRepository, times(2)).save(any(ReviewSymptom.class));
        BDDMockito.verify(reviewImageRepository, times(2)).save(any(ReviewImage.class));

    }

    @Test
    @DisplayName("병원에 해당하는 리뷰 요약을 조회할 수 있다.")
    void getReviewSummaryByHospitalId() {
        //given
        final Long hospitalId = 1L;
        final Review review1 = Review.builder()
                .hospitalId(1L)
                .build();
        final Review review2 = Review.builder()
                .hospitalId(1L)
                .build();
        final Review review3 = Review.builder()
                .hospitalId(1L)
                .build();

        ReflectionTestUtils.setField(review1, "id", 1L);
        ReflectionTestUtils.setField(review2, "id", 2L);
        ReflectionTestUtils.setField(review3, "id", 3L);

        final List<Review> reviews = new ArrayList<>(List.of(review1, review2, review3));
        final List<Long> reviewIds = new ArrayList<>(List.of(review1.getId(), review2.getId(), review3.getId()));

        final ReviewSummaryOption reviewSummaryOption1 = ReviewSummaryOption.builder()
                .label("좋은 리뷰")
                .isGood(true)
                .build();

        final ReviewSummaryOption reviewSummaryOption2 = ReviewSummaryOption.builder()
                .label("나쁜 리뷰")
                .isGood(false)
                .build();

        ReflectionTestUtils.setField(reviewSummaryOption1, "id", 1L);
        ReflectionTestUtils.setField(reviewSummaryOption2, "id", 2L);

        final List<ReviewSummaryOption> reviewSummaryOptions = new ArrayList<>(List.of(reviewSummaryOption1, reviewSummaryOption2));

        final ReviewSummaryResponse reviewSummaryResponse1 = ReviewSummaryResponse.of(
                1L,
                "좋은 리뷰",
                2
        );

        final ReviewSummaryResponse reviewSummaryResponse2 = ReviewSummaryResponse.of(
                2L,
                "나쁜 리뷰",
                1
        );

        final ReviewSummaryListResponse expected = ReviewSummaryListResponse.of(
                List.of(reviewSummaryResponse1),
                List.of(reviewSummaryResponse2)
        );

        BDDMockito.given(reviewRepository.findAllByHospitalId(hospitalId)).willReturn(reviews);
        BDDMockito.given(reviewSummaryOptionRepository.findAll()).willReturn(reviewSummaryOptions);
        BDDMockito.given(reviewSummaryRepository.countByReviewIdInAndReviewSummaryOptionId(reviewIds, reviewSummaryOption1.getId())).willReturn(2);
        BDDMockito.given(reviewSummaryRepository.countByReviewIdInAndReviewSummaryOptionId(reviewIds, reviewSummaryOption2.getId())).willReturn(1);

        //when
        final ReviewSummaryListResponse actual = reviewService.getReviewSummaryList(hospitalId);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        Assertions.assertThat(actual.goodReviews().getFirst().label()).isEqualTo("좋은 리뷰");
        Assertions.assertThat(actual.badReviews().getFirst().label()).isEqualTo("나쁜 리뷰");
    }

    @Test
    @DisplayName("리뷰 요약 옵션 목록을 조회할 수 있다.")
    void getReviewSummaryOptionList() {
        //given
        final ReviewSummaryOption reviewSummaryOption1 = ReviewSummaryOption.builder()
                .label("좋은 리뷰1")
                .isGood(true)
                .build();

        final ReviewSummaryOption reviewSummaryOption2 = ReviewSummaryOption.builder()
                .label("나쁜 리뷰1")
                .isGood(false)
                .build();

        final ReviewSummaryOption reviewSummaryOption3 = ReviewSummaryOption.builder()
                .label("나쁜 리뷰2")
                .isGood(false)
                .build();

        ReflectionTestUtils.setField(reviewSummaryOption1, "id", 1L);
        ReflectionTestUtils.setField(reviewSummaryOption2, "id", 2L);
        ReflectionTestUtils.setField(reviewSummaryOption3, "id", 3L);

        final List<ReviewSummaryOption> reviewSummaryOptions = new ArrayList<>(List.of(reviewSummaryOption1, reviewSummaryOption2, reviewSummaryOption3));

        final ReviewSummaryOptionResponse reviewSummaryResponse1 = ReviewSummaryOptionResponse.of(
                1L,
                "좋은 리뷰1"
        );

        final ReviewSummaryOptionResponse reviewSummaryResponse2 = ReviewSummaryOptionResponse.of(
                2L,
                "나쁜 리뷰1"
        );

        final ReviewSummaryOptionResponse reviewSummaryResponse3 = ReviewSummaryOptionResponse.of(
                3L,
                "나쁜 리뷰2"
        );

        BDDMockito.given(reviewSummaryOptionRepository.findAll()).willReturn(reviewSummaryOptions);

        final ReviewSummaryOptionListResponse expected = ReviewSummaryOptionListResponse.of(
                List.of(reviewSummaryResponse1),
                List.of(reviewSummaryResponse2, reviewSummaryResponse3)
        );

        //when
        final ReviewSummaryOptionListResponse actual = reviewService.getReviewSummaryOptionList();

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("리뷰를 삭제할 수 있다.")
    void deleteReview() {
        //given
        final Long memberId = 1L;
        final Long reviewId = 2L;

        final Hospital hospital = Hospital.builder()
                .reviewCount(10)
                .build();

        final Review review = Review.builder()
                .memberId(memberId)
                .hospitalId(1L)
                .build();

        final ReviewImage reviewImage1 = ReviewImage.builder()
                .image("image1")
                .reviewId(1L)
                .build();

        final ReviewImage reviewImage2 = ReviewImage.builder()
                .image("image2")
                .reviewId(1L)
                .build();

        final ReviewImage reviewImage3 = ReviewImage.builder()
                .image("image3")
                .reviewId(1L)
                .build();

        final List<ReviewImage> reviewImages = new ArrayList<>(List.of(reviewImage1, reviewImage2, reviewImage3));

        BDDMockito.given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        BDDMockito.given(reviewImageRepository.findAllByReviewId(reviewId)).willReturn(reviewImages);
        BDDMockito.given(memberDataS3Client.deletePresignedUrl(reviewImage1.getImage())).willReturn("presignedUrl1");
        BDDMockito.given(memberDataS3Client.deletePresignedUrl(reviewImage2.getImage())).willReturn("presignedUrl2");
        BDDMockito.given(memberDataS3Client.deletePresignedUrl(reviewImage3.getImage())).willReturn("presignedUrl3");
        BDDMockito.given(hospitalRepository.findById(review.getHospitalId())).willReturn(Optional.of(hospital));

        final ReviewImageDeleteListResponse expected = ReviewImageDeleteListResponse.of(
                List.of("presignedUrl1", "presignedUrl2", "presignedUrl3")
        );

        //when
        final ReviewImageDeleteListResponse actual = reviewService.deleteReview(memberId, reviewId);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        Assertions.assertThat(hospital.getReviewCount()).isEqualTo(9);
        Mockito.verify(reviewSummaryRepository, Mockito.times(1)).deleteAllByReviewId(reviewId);
        Mockito.verify(reviewImageRepository, Mockito.times(1)).deleteAllByReviewId(reviewId);

    }

    @Test

    @DisplayName("병원 ID 필터링 조건으로 병원 리뷰 목록을 조회할 수 있다.")
    void getHospitalReviewListWithHospitalIdFilter() {

        //given
        final Long hospitalId = 1L;
        final Long memberId = 1L;
        final Long reviewId = 1L;
        final Long breedId = 1L;
        final Long animalId = 1L;
        final Long diseaseId = 1L;
        final Long purposeId = 1L;
        final LocalDateTime visitedAt = LocalDateTime.parse("2025-07-12T15:20:19");

        final Review review = Review.builder().visitedAt(visitedAt).hospitalId(hospitalId).memberId(memberId).breedId(breedId).diseaseId(diseaseId).purposeId(purposeId).build();
        ReflectionTestUtils.setField(review, "id", reviewId);
        final Hospital hospital = Hospital.builder().name("테스트 병원").build();
        ReflectionTestUtils.setField(hospital, "id", hospitalId);
        final Member member = Member.builder().nickname("테스트유저").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        final Pet pet = Pet.builder().memberId(memberId).breedId(breedId).age(1).build();
        ReflectionTestUtils.setField(pet, "id", 1L);
        final Breed breed = Breed.builder().name("테스트견종").animalId(animalId).build();
        ReflectionTestUtils.setField(breed, "id", breedId);
        final Animal animal = Animal.builder().name("개").build();
        ReflectionTestUtils.setField(animal, "id", animalId);
        final Disease disease = Disease.builder().name("테스트질병").build();
        ReflectionTestUtils.setField(disease, "id", diseaseId);
        final VisitPurpose visitPurpose = VisitPurpose.builder().name("테스트목적").build();
        ReflectionTestUtils.setField(visitPurpose, "id", purposeId);

        BDDMockito.given(reviewRepository.findBySearchCondition(any())).willReturn(List.of(review));
        BDDMockito.given(hospitalRepository.findAllById(any())).willReturn(List.of(hospital));
        BDDMockito.given(memberRepository.findAllById(any())).willReturn(List.of(member));
        BDDMockito.given(petRepository.findAllByMemberIdIn(any(java.util.Set.class))).willReturn(List.of(pet));
        BDDMockito.given(breedRepository.findAllById(any())).willReturn(List.of(breed));
        BDDMockito.given(animalRepository.findAllById(any())).willReturn(List.of(animal));
        BDDMockito.given(diseaseRepository.findAllById(any())).willReturn(List.of(disease));
        BDDMockito.given(hospitalVisitPurposeRepository.findAll()).willReturn(List.of(visitPurpose));
        BDDMockito.given(hospitalRepository.findById(hospitalId)).willReturn(Optional.of(Hospital.builder().reviewCount(1).build()));
        BDDMockito.given(reviewImageRepository.findAllByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSymptomRepository.findByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSummaryRepository.findByReviewIdIn(any())).willReturn(List.of());

        //when
        final HospitalReviewListResponse actual = reviewService.getHospitalReviewList(hospitalId, null, null, 5, null, null, null, memberId);
        //then

        Assertions.assertThat(actual.reviews()).hasSize(1);
        Assertions.assertThat(actual.reviews().get(0).nickname()).isEqualTo("테스트유저");
        Assertions.assertThat(actual.reviewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("bodyId, locationId, locationType 필터링 조건으로 병원 리뷰 목록을 조회할 수 있다.")
    void getHospitalReviewListWithBodyAndLocationFilter() {
        //given
        final Long hospitalId = null;
        final Long memberId = 1L;
        final Long reviewId = 1L;
        final Long breedId = 1L;
        final Long animalId = 1L;
        final Long diseaseId = 1L;
        final Long purposeId = 1L;
        final Long reviewHospitalId = 2L;
        final LocalDateTime visitedAt = LocalDateTime.parse("2025-07-12T15:20:19");

        final Review review = Review.builder().visitedAt(visitedAt).hospitalId(reviewHospitalId).memberId(memberId).breedId(breedId).diseaseId(diseaseId).purposeId(purposeId).build();
        ReflectionTestUtils.setField(review, "id", reviewId);
        final Hospital hospital = Hospital.builder().name("테스트 병원").build();
        ReflectionTestUtils.setField(hospital, "id", reviewHospitalId);
        final Member member = Member.builder().nickname("테스트유저").build();
        ReflectionTestUtils.setField(member, "id", memberId);
        final Pet pet = Pet.builder().memberId(memberId).breedId(breedId).age(1).build();
        ReflectionTestUtils.setField(pet, "id", 1L);
        final Breed breed = Breed.builder().name("테스트견종").animalId(animalId).build();
        ReflectionTestUtils.setField(breed, "id", breedId);
        final Animal animal = Animal.builder().name("개").build();
        ReflectionTestUtils.setField(animal, "id", animalId);
        final Disease disease = Disease.builder().name("테스트질병").build();
        ReflectionTestUtils.setField(disease, "id", diseaseId);
        final VisitPurpose visitPurpose = VisitPurpose.builder().name("테스트목적").build();
        ReflectionTestUtils.setField(visitPurpose, "id", purposeId);

        BDDMockito.given(reviewRepository.findBySearchCondition(any())).willReturn(List.of(review));
        BDDMockito.given(hospitalRepository.findAllById(any())).willReturn(List.of(hospital));
        BDDMockito.given(memberRepository.findAllById(any())).willReturn(List.of(member));
        BDDMockito.given(petRepository.findAllByMemberIdIn(any(java.util.Set.class))).willReturn(List.of(pet));
        BDDMockito.given(breedRepository.findAllById(any())).willReturn(List.of(breed));
        BDDMockito.given(animalRepository.findAllById(any())).willReturn(List.of(animal));
        BDDMockito.given(diseaseRepository.findAllById(any())).willReturn(List.of(disease));
        BDDMockito.given(hospitalVisitPurposeRepository.findAll()).willReturn(List.of(visitPurpose));
        BDDMockito.given(reviewImageRepository.findAllByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSymptomRepository.findByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSummaryRepository.findByReviewIdIn(any())).willReturn(List.of());


        //when
        final HospitalReviewListResponse actual = reviewService.getHospitalReviewList(hospitalId, null, null, 5, 1L, 1L, LocationType.CITY, memberId);

        //then
        Assertions.assertThat(actual.reviews()).hasSize(1);
        Assertions.assertThat(actual.reviews().get(0).nickname()).isEqualTo("테스트유저");
        Assertions.assertThat(actual.reviewCount()).isNull();
    }

    @Test
    @DisplayName("병원 리뷰 목록을 페이지네이션으로 조회할 수 있다.")
    void getHospitalReviewListWithPagination() {
        // given
        final Long hospitalId = 100L;
        final int pageSize = 5;
        final LocalDateTime visitedAt = LocalDateTime.parse("2025-07-12T15:20:19");

        final Member member = Member.builder().nickname("테스트유저").build();
        ReflectionTestUtils.setField(member, "id", 1L); // Assuming memberId 1 is always present for simplicity

        final List<Review> allReviews = new ArrayList<>();
        for (long i = 10; i >= 1; i--) {
            final Review review = Review.builder()
                    .visitedAt(visitedAt)
                    .hospitalId(hospitalId)
                    .memberId(member.getId())
                    .breedId(1L)
                    .diseaseId(1L)
                    .purposeId(1L)
                    .build();
            ReflectionTestUtils.setField(review, "id", i);
            allReviews.add(review);
        }

        final Hospital hospital = Hospital.builder().name("테스트 병원").reviewCount(allReviews.size()).build();
        ReflectionTestUtils.setField(hospital, "id", hospitalId);

        final Pet pet = Pet.builder().memberId(member.getId()).breedId(1L).age(1).build();
        ReflectionTestUtils.setField(pet, "id", 1L);
        final Breed breed = Breed.builder().name("테스트견종").animalId(1L).build();
        ReflectionTestUtils.setField(breed, "id", 1L);
        final Animal animal = Animal.builder().name("개").build();
        ReflectionTestUtils.setField(animal, "id", 1L);
        final Disease disease = Disease.builder().name("테스트질병").build();
        ReflectionTestUtils.setField(disease, "id", 1L);
        final VisitPurpose visitPurpose = VisitPurpose.builder().name("테스트목적").build();
        ReflectionTestUtils.setField(visitPurpose, "id", 1L);

        BDDMockito.given(reviewRepository.findBySearchCondition(any())).willAnswer(invocation -> {
            final ReviewSearchCondition condition = invocation.getArgument(0);
            final Long cursorId = condition.cursorId();
            final int size = condition.size();

            if (cursorId == null) {
                return allReviews.subList(0, Math.min(size, allReviews.size()));
            } else {
                final int cursorIndex = allReviews.indexOf(allReviews.stream()
                        .filter(r -> r.getId().equals(cursorId))
                        .findFirst()
                        .orElse(null));
                if (cursorIndex == -1) {
                    return List.of();
                }
                final int startIndex = cursorIndex + 1;
                return allReviews.subList(startIndex, Math.min(startIndex + size, allReviews.size()));
            }
        });

        BDDMockito.given(hospitalRepository.findById(hospitalId)).willReturn(Optional.of(hospital));
        BDDMockito.given(hospitalRepository.findAllById(any())).willReturn(List.of(hospital));
        BDDMockito.given(memberRepository.findAllById(any())).willReturn(List.of(member));
        BDDMockito.given(petRepository.findAllByMemberIdIn(any(java.util.Set.class))).willReturn(List.of(pet));
        BDDMockito.given(breedRepository.findAllById(any())).willReturn(List.of(breed));
        BDDMockito.given(animalRepository.findAllById(any())).willReturn(List.of(animal));
        BDDMockito.given(diseaseRepository.findAllById(any())).willReturn(List.of(disease));
        BDDMockito.given(hospitalVisitPurposeRepository.findAll()).willReturn(List.of(visitPurpose));
        BDDMockito.given(reviewImageRepository.findAllByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSymptomRepository.findByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSummaryRepository.findByReviewIdIn(any())).willReturn(List.of());

        final HospitalReviewListResponse firstPage = reviewService.getHospitalReviewList(hospitalId, null, null, pageSize, null, null, null, member.getId());

        Assertions.assertThat(firstPage.reviews()).hasSize(pageSize);
        Assertions.assertThat(firstPage.reviews().get(0).id()).isEqualTo(10L);
        Assertions.assertThat(firstPage.reviews().get(4).id()).isEqualTo(6L);
        Assertions.assertThat(firstPage.cursorId()).isEqualTo(6L);
        Assertions.assertThat(firstPage.reviewCount()).isEqualTo(10);

        final HospitalReviewListResponse secondPage = reviewService.getHospitalReviewList(hospitalId, null, firstPage.cursorId(), pageSize, null, null, null, member.getId());

        Assertions.assertThat(secondPage.reviews()).hasSize(pageSize);
        Assertions.assertThat(secondPage.reviews().get(0).id()).isEqualTo(5L);
        Assertions.assertThat(secondPage.reviews().get(4).id()).isEqualTo(1L);
        Assertions.assertThat(secondPage.cursorId()).isEqualTo(1L);
        Assertions.assertThat(secondPage.reviewCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("bodyId가 있는 경우 페이지네이션으로 리뷰 목록을 조회할 수 있다.")
    void getHospitalReviewListWithPaginationAndBodyId() {
        // given
        final Long hospitalId = 100L;
        final Long bodyId = 1L;
        final int pageSize = 3;
        final LocalDateTime visitedAt = LocalDateTime.parse("2025-07-12T15:20:19");

        final Member member = Member.builder().nickname("테스트유저").build();
        ReflectionTestUtils.setField(member, "id", 1L);

        final List<Review> allReviews = new ArrayList<>();
        for (long i = 7; i >= 1; i--) {
            final Review review = Review.builder()
                    .visitedAt(visitedAt)
                    .hospitalId(hospitalId)
                    .memberId(member.getId())
                    .breedId(1L)
                    .diseaseId(1L)
                    .purposeId(1L)
                    .build();
            ReflectionTestUtils.setField(review, "id", i);
            allReviews.add(review);
        }

        final Hospital hospital = Hospital.builder().name("테스트 병원").build();
        ReflectionTestUtils.setField(hospital, "id", hospitalId);
        final Pet pet = Pet.builder().memberId(member.getId()).breedId(1L).age(1).build();
        ReflectionTestUtils.setField(pet, "id", 1L);
        final Breed breed = Breed.builder().name("테스트견종").animalId(1L).build();
        ReflectionTestUtils.setField(breed, "id", 1L);
        final Animal animal = Animal.builder().name("개").build();
        ReflectionTestUtils.setField(animal, "id", 1L);
        final Disease disease = Disease.builder().name("테스트질병").build();
        ReflectionTestUtils.setField(disease, "id", 1L);
        final VisitPurpose visitPurpose = VisitPurpose.builder().name("테스트목적").build();
        ReflectionTestUtils.setField(visitPurpose, "id", 1L);

        BDDMockito.given(reviewRepository.findBySearchCondition(any())).willAnswer(invocation -> {
            final com.cocos.cocos.api.review.dto.query.ReviewSearchCondition condition = invocation.getArgument(0);
            final Long cursorId = condition.cursorId();
            final int size = condition.size();
            final Long actualBodyId = condition.bodyId();

            if (!bodyId.equals(actualBodyId)) {
                return List.of();
            }

            if (cursorId == null) {
                return allReviews.subList(0, Math.min(size, allReviews.size()));
            } else {
                final int cursorIndex = allReviews.indexOf(allReviews.stream()
                        .filter(r -> r.getId().equals(cursorId))
                        .findFirst()
                        .orElse(null));
                if (cursorIndex == -1) {
                    return List.of();
                }
                final int startIndex = cursorIndex + 1;
                return allReviews.subList(startIndex, Math.min(startIndex + size, allReviews.size()));
            }
        });

        BDDMockito.given(hospitalRepository.findAllById(any())).willReturn(List.of(hospital));
        BDDMockito.given(memberRepository.findAllById(any())).willReturn(List.of(member));
        BDDMockito.given(petRepository.findAllByMemberIdIn(any(java.util.Set.class))).willReturn(List.of(pet));
        BDDMockito.given(breedRepository.findAllById(any())).willReturn(List.of(breed));
        BDDMockito.given(animalRepository.findAllById(any())).willReturn(List.of(animal));
        BDDMockito.given(diseaseRepository.findAllById(any())).willReturn(List.of(disease));
        BDDMockito.given(hospitalVisitPurposeRepository.findAll()).willReturn(List.of(visitPurpose));
        BDDMockito.given(hospitalRepository.findById(hospitalId)).willReturn(Optional.of(Hospital.builder().reviewCount(allReviews.size()).build()));
        BDDMockito.given(reviewImageRepository.findAllByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSymptomRepository.findByReviewIdIn(any())).willReturn(List.of());
        BDDMockito.given(reviewSummaryRepository.findByReviewIdIn(any())).willReturn(List.of());

        final HospitalReviewListResponse firstPage = reviewService.getHospitalReviewList(hospitalId, null, null, pageSize, bodyId, null, null, member.getId());
        Assertions.assertThat(firstPage.reviews()).hasSize(pageSize);
        Assertions.assertThat(firstPage.reviews().stream().map(HospitalReviewResponse::id).toList()).contains(7L, 6L, 5L);
        Assertions.assertThat(firstPage.cursorId()).isEqualTo(5L);

        final HospitalReviewListResponse secondPage = reviewService.getHospitalReviewList(hospitalId, null, firstPage.cursorId(), pageSize, bodyId, null, null, member.getId());
        Assertions.assertThat(secondPage.reviews()).hasSize(pageSize);
        Assertions.assertThat(secondPage.reviews().stream().map(HospitalReviewResponse::id).toList()).contains(4L, 3L, 2L);
        Assertions.assertThat(secondPage.cursorId()).isEqualTo(2L);

        final HospitalReviewListResponse lastPage = reviewService.getHospitalReviewList(hospitalId, null, secondPage.cursorId(), pageSize, bodyId, null, null, member.getId());
        Assertions.assertThat(lastPage.reviews()).hasSize(1);
        Assertions.assertThat(lastPage.reviews().get(0).id()).isEqualTo(1L);
        Assertions.assertThat(lastPage.cursorId()).isEqualTo(1L);
    }
}
