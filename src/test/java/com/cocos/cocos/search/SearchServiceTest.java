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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("최근 검색어 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SearchServiceTest {

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
}
