package com.cocos.cocos.search;

import com.cocos.cocos.api.search.service.SearchService;
import com.cocos.cocos.config.JpaAuditingConfig;
import com.cocos.cocos.config.QuerydslConfig;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.enums.search.SearchType;
import com.cocos.cocos.db.search.entity.Search;
import org.assertj.core.api.Assertions;
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
@Import({SearchService.class,JpaAuditingConfig.class, QuerydslConfig.class })
@ActiveProfiles("test")
class SearchConcurrencyTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchRepository searchRepository;

    @Test
    @DisplayName("동시에 검색어 저장 요청이 와도 insert는 한 번만 발생한다")
    void addSearchConcurrency() throws InterruptedException {
        // given
        final int threadCount = 10;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final Long memberId = 1L;

        final String keyword = "피부과";
        final SearchType type = SearchType.HOSPITAL;

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
        final List<Search> results = searchRepository.findAllByMemberIdAndKeywordAndSearchType(memberId, keyword, type);
        Assertions.assertThat(results).hasSize(1);
    }
}
