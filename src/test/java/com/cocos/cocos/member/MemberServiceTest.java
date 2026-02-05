package com.cocos.cocos.member;

import com.cocos.cocos.api.member.dto.response.MemberRecentReviewResponse;
import com.cocos.cocos.api.member.dto.response.MemberReviewTermsAgreeResponse;
import com.cocos.cocos.api.member.service.MemberService;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.review.entity.Review;
import com.cocos.cocos.db.review.repository.ReviewRepository;
import com.cocos.cocos.enums.hospital.VisitTimeUnit;
import com.cocos.cocos.enums.message.FailMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("멤버 서비스 테스트")
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private DiseaseRepository diseaseRepository;
    @Mock
    private BodyRepository bodyRepository;
    @Mock
    private Clock clock;

    private static final LocalDateTime FIXED_NOW =
            LocalDateTime.of(2025, 10, 26, 10, 0);
    public static final DateTimeFormatter VISITED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private void setupClock() {
        Clock fixedClock = Clock.fixed(
                FIXED_NOW.toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );
        given(clock.instant()).willReturn(fixedClock.instant());
        given(clock.getZone()).willReturn(fixedClock.getZone());
    }

    @Test
    @DisplayName("리뷰 약관 동의를 할 수 있다")
    void updateReviewTermsAgree() {
        final Long memberId = 1L;
        final Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "isReviewTermsAgree", false);

        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));

        memberService.updateReviewTermsAgree(memberId);

        assertThat(member.isReviewTermsAgree()).isTrue();
    }

    @Test
    @DisplayName("리뷰 약관 동의 여부를 조회할 수 있다")
    void getReviewTermsAgree() {
        final Long memberId = 1L;
        final Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "isReviewTermsAgree", true);

        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));

        MemberReviewTermsAgreeResponse response =
                memberService.getMemberReviewTermsAgree(memberId);

        assertThat(response.isReviewTermsAgree()).isTrue();
    }

    @Test
    @DisplayName("멤버를 찾을 수 없으면 예외를 발생시킨다")
    void getRecentReview_memberNotFound() {
        final Long memberId = 1L;
        given(memberRepository.findById(memberId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                memberService.getRecentVisitedReview(null, memberId)
        ).isInstanceOf(CocosException.class)
                .hasFieldOrPropertyWithValue(
                        "failMessage",
                        FailMessage.NOT_FOUND_MEMBER
                );

        verify(reviewRepository, never())
                .findTopByMemberIdAndDiseaseIdIsNotNullOrderByVisitedAtDesc(anyLong());
    }

    @Test
    @DisplayName("리뷰가 한 달 이내면 DAY 단위를 반환한다")
    void getRecentReview_withinMonth_returnsDay() {
        setupClock();

        final Long memberId = 1L;
        final String bodyName = "Eye";
        final String visitedAt = FIXED_NOW.minusDays(15).format(VISITED_AT_FORMATTER);

        givenRecentReview(memberId, bodyName, visitedAt);

        final MemberRecentReviewResponse response =
                memberService.getRecentVisitedReview(null, memberId);

        assertThat(response.diseaseBody()).isEqualTo(bodyName);
        assertThat(response.timeSinceVisit().value()).isEqualTo(15);
        assertThat(response.timeSinceVisit().unit())
                .isEqualTo(VisitTimeUnit.DAY);
    }

    @Test
    @DisplayName("리뷰가 한 달 이상이면 MONTH 단위를 반환한다")
    void getRecentReview_overMonth_returnsMonth() {
        setupClock();

        final Long memberId = 1L;
        final String bodyName = "Leg";
        final String visitedAt = FIXED_NOW.minusDays(60).format(VISITED_AT_FORMATTER);

        givenRecentReview(memberId, bodyName, visitedAt);

        final MemberRecentReviewResponse response =
                memberService.getRecentVisitedReview(null, memberId);

        assertThat(response.timeSinceVisit().value()).isEqualTo(2);
        assertThat(response.timeSinceVisit().unit())
                .isEqualTo(VisitTimeUnit.MONTH);
    }

    private void givenRecentReview(
            Long memberId,
            String bodyName,
            String visitedAt
    ) {
        final Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", memberId);

        final Review review = Review.builder()
                .memberId(memberId)
                .diseaseId(10L)
                .build();
        ReflectionTestUtils.setField(review, "visitedAt", visitedAt);

        final Disease disease = Disease.builder().bodyId(20L).build();
        final Body body = Body.builder().name(bodyName).build();

        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));
        given(reviewRepository
                .findTopByMemberIdAndDiseaseIdIsNotNullOrderByVisitedAtDesc(memberId))
                .willReturn(Optional.of(review));
        given(diseaseRepository.findById(10L))
                .willReturn(Optional.of(disease));
        given(bodyRepository.findById(20L))
                .willReturn(Optional.of(body));
    }
}
