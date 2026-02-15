package com.cocos.cocos.search;

import com.cocos.cocos.api.search.dto.response.KeywordResponse;
import com.cocos.cocos.api.search.dto.response.SearchResponse;
import com.cocos.cocos.api.search.service.SearchService;
import com.cocos.cocos.api.search.service.SearchWriteTxExecutor;
import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.enums.search.SearchType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("최근 검색어 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private SearchWriteTxExecutor searchWriteTxExecutor;

    @Test
    @DisplayName("최근에 검색한 커뮤니티 검색어 5개를 보여줄 수 있다.")
    void getSearch() {
        //given
        final Long memberId = 1L;
        final Search search1 = Search.builder()
                .memberId(memberId)
                .keyword("keyword1")
                .searchType(SearchType.COMMUNITY)
                .build();
        final Search search2 = Search.builder()
                .memberId(memberId)
                .keyword("keyword1")
                .searchType(SearchType.COMMUNITY)
                .build();
        final SearchType searchType = SearchType.COMMUNITY;

        final List<Search> searchList = new ArrayList<>(List.of(search1, search2));
        BDDMockito.given(searchRepository.findTop5ByMemberIdAndSearchTypeOrderByUpdatedAtDesc(memberId, searchType)).willReturn(searchList);

        final SearchResponse expected = SearchResponse.of(
                searchList.stream()
                        .map(search -> KeywordResponse.of(search.getId(), search.getKeyword()))
                        .toList()
        );

        //when
        final SearchResponse actual = searchService.getSearchByType(memberId, searchType);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("정상 저장(또는 기존 검색어 업데이트)은 저장 트랜잭션에서 처리한다")
    void addSearchSuccess() {
        // given
        final Long memberId = 1L;
        final String keyword = "피부과";
        final SearchType searchType = SearchType.HOSPITAL;

        // when
        searchService.addSearch(memberId, keyword, searchType);

        // then
        Mockito.verify(searchWriteTxExecutor, Mockito.times(1))
                .saveOrUpdateExisting(memberId, keyword, searchType);
        Mockito.verify(searchWriteTxExecutor, Mockito.never())
                .recoverDuplicate(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
    }

    @Test
    @DisplayName("중복키 충돌이 발생하면 기존 검색어를 조회해 업데이트한다")
    void addSearchOnDuplicateKey() {
        // given
        final Long memberId = 1L;
        final String keyword = "피부과";
        final SearchType searchType = SearchType.HOSPITAL;

        BDDMockito.willThrow(new DataIntegrityViolationException("duplicate key"))
                .given(searchWriteTxExecutor).saveOrUpdateExisting(memberId, keyword, searchType);
        BDDMockito.given(searchWriteTxExecutor.recoverDuplicate(memberId, keyword, searchType))
                .willReturn(true);

        // when
        searchService.addSearch(memberId, keyword, searchType);

        // then
        Mockito.verify(searchWriteTxExecutor, Mockito.times(1))
                .saveOrUpdateExisting(memberId, keyword, searchType);
        Mockito.verify(searchWriteTxExecutor, Mockito.times(1))
                .recoverDuplicate(memberId, keyword, searchType);
    }

    @Test
    @DisplayName("중복키 충돌 후 복구에 실패하면 원래 예외를 다시 던진다")
    void throwExceptionWhenDuplicateRecoveryFails() {
        // given
        final Long memberId = 1L;
        final String keyword = "피부과";
        final SearchType searchType = SearchType.HOSPITAL;
        final DataIntegrityViolationException duplicateException =
                new DataIntegrityViolationException("duplicate key");

        BDDMockito.willThrow(duplicateException)
                .given(searchWriteTxExecutor).saveOrUpdateExisting(memberId, keyword, searchType);
        BDDMockito.given(searchWriteTxExecutor.recoverDuplicate(memberId, keyword, searchType))
                .willReturn(false);

        // when & then
        Assertions.assertThatThrownBy(() -> searchService.addSearch(memberId, keyword, searchType))
                .isSameAs(duplicateException);
    }

    @Test
    @DisplayName("중복키가 아닌 무결성 예외는 복구를 시도하고 실패 시 그대로 던진다")
    void throwExceptionWhenIntegrityViolationIsNotDuplicate() {
        // given
        final Long memberId = 1L;
        final String keyword = "피부과";
        final SearchType searchType = SearchType.HOSPITAL;
        final DataIntegrityViolationException integrityException =
                new DataIntegrityViolationException("NOT NULL constraint failed");

        BDDMockito.willThrow(integrityException)
                .given(searchWriteTxExecutor).saveOrUpdateExisting(memberId, keyword, searchType);

        // when & then
        Assertions.assertThatThrownBy(() -> searchService.addSearch(memberId, keyword, searchType))
                .isSameAs(integrityException);
        Mockito.verify(searchWriteTxExecutor, Mockito.times(1))
                .recoverDuplicate(memberId, keyword, searchType);
    }
}
