package com.cocos.cocos.review;

import com.cocos.cocos.api.review.dto.response.ReviewAddResponse;
import com.cocos.cocos.api.review.dto.response.ReviewImageDeleteListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionListResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryOptionResponse;
import com.cocos.cocos.api.review.dto.response.ReviewSummaryResponse;
import com.cocos.cocos.api.review.service.ReviewService;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;

import com.cocos.cocos.db.review.db.Review;
import com.cocos.cocos.db.review.db.ReviewImage;
import com.cocos.cocos.db.review.db.ReviewSummary;
import com.cocos.cocos.db.review.db.ReviewSummaryOption;
import com.cocos.cocos.db.review.db.ReviewSymptom;
import com.cocos.cocos.db.review.repository.ReviewImageRepository;
import com.cocos.cocos.db.review.repository.ReviewRepository;
import com.cocos.cocos.db.review.repository.ReviewSummaryOptionRepository;
import com.cocos.cocos.db.review.repository.ReviewSummaryRepository;
import com.cocos.cocos.db.review.repository.ReviewSymptomRepository;
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

    @Test
    @DisplayName("리뷰를 작성할 수 있다.")
    void addReview() {
        //given
        final Long memberId = 1L;
        final Long hospitalId = 2L;
        final Long breedId = 1L;
        final Gender gender = Gender.F;
        final double weight = 7.0;
        final String visitedAt = "2025.04.22";
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

        BDDMockito.given(hospitalRepository.findById(hospitalId))
                .willReturn(Optional.of(hospital));

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
}
