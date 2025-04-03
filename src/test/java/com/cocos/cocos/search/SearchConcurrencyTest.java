package com.cocos.cocos.search;

import com.cocos.cocos.api.search.service.SearchService;
import com.cocos.cocos.config.JpaAuditingConfig;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.enums.member.Platform;
import com.cocos.cocos.enums.search.SearchType;
import com.cocos.cocos.db.search.entity.Search;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DataJpaTest
@Import({SearchService.class,JpaAuditingConfig.class })
@ActiveProfiles("test")
class SearchConcurrencyTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchRepository searchRepository;

    private Long memberId;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .nickname("테스트유저")
                .email("test@cocos.com")
                .image("")
                .platform(Platform.KAKAO)
                .sub("")
                .isAdmin(false)
                .build();

        member = memberRepository.save(member);
        memberId = member.getId();
    }

    @Test
    @DisplayName("동시에 검색어 저장 요청이 와도 insert는 한 번만 발생한다")
    void addSearchConcurrency() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String keyword = "피부과";
        SearchType type = SearchType.HOSPITAL;

        // when: 동시에 10개 요청
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    searchService.addSearch(memberId, keyword, type);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } finally {
            executor.shutdown();
        }

        // then
        List<Search> results = searchRepository.findAllByMemberIdAndKeywordAndSearchType(memberId, keyword, type);
        Assertions.assertThat(results).hasSize(1);
    }
}
