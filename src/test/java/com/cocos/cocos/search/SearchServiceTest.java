package com.cocos.cocos.search;

import com.cocos.cocos.api.search.dto.response.KeywordResponse;
import com.cocos.cocos.api.search.dto.response.SearchResponse;
import com.cocos.cocos.api.search.service.SearchService;
import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.enums.search.SearchType;
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
    @DisplayName("이미 존재하는 검색어가 있을 경우 업데이트가 된다")
    void addSearch() {
        // given
        final Long memberId = 1L;
        final String keyword = "피부과";
        final SearchType searchType = SearchType.HOSPITAL;

        final Search existingSearch = Mockito.mock(Search.class);
        BDDMockito.given(searchRepository.findWithLockByMemberIdAndKeywordAndSearchType(memberId, keyword, searchType))
                .willReturn(existingSearch);

        // when
        searchService.addSearch(memberId, keyword, searchType);

        // then
        Mockito.verify(existingSearch, Mockito.times(1)).updateTime();
        Mockito.verify(searchRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("동일한 검색어가 없으면 새로 저장한다")
    void addSearchIfSearchDoesNotExist() {
        // given
        final Long memberId = 1L;
        final String keyword = "피부과";
        final SearchType searchType = SearchType.HOSPITAL;

        BDDMockito.given(searchRepository.findWithLockByMemberIdAndKeywordAndSearchType(memberId, keyword, searchType))
                .willReturn(null);

        // when
        searchService.addSearch(memberId, keyword, searchType);

        // then
        Mockito.verify(searchRepository, Mockito.times(1)).save(Mockito.any(Search.class));
    }
}
