package com.cocos.cocos.member;

import com.cocos.cocos.api.member.dto.response.MemberReviewTermsAgreeResponse;
import com.cocos.cocos.api.member.service.MemberService;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("지역 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("리뷰 약관 동의를 할 수 있다.")
    void addReviewAgree() {
        //given
        final Member member = Member.builder()
                .build();
        final Long memberId = 1L;

        ReflectionTestUtils.setField(member, "isReviewTermsAgree", false);
        ReflectionTestUtils.setField(member, "id", memberId);

        //when
        memberService.updateReviewTermsAgree(memberId);

        //then
        Assertions.assertThat(member.getIsReviewTermsAgree()).isEqualTo(true);
    }

    @Test
    @DisplayName("리뷰 약관 동의 여부를 조회 할 수 있다.")
    void getReviewAgree() {
        //given
        final Member member = Member.builder()
                .build();
        final Long memberId = 1L;

        ReflectionTestUtils.setField(member, "isReviewTermsAgree", true);
        ReflectionTestUtils.setField(member, "id", memberId);

        final MemberReviewTermsAgreeResponse expected = MemberReviewTermsAgreeResponse.of(member.getIsReviewTermsAgree());

        //when
        final MemberReviewTermsAgreeResponse actual = memberService.getReviewTermsAgree();

        //then
        Assertions.assertThat(expected).usingRecursiveAssertion().isEqualTo(actual);

    }
}
