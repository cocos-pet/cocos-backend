package com.cocos.cocos.api.search.service;

import com.cocos.cocos.api.search.dto.response.KeywordResponse;
import com.cocos.cocos.api.search.dto.response.SearchResponse;
import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.enums.search.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    @Transactional
    public void addSearch(final Long memberId, final String keyword, final SearchType searchType) {
        final Search search = searchRepository.findWithLockByMemberIdAndKeywordAndSearchType(memberId, keyword, searchType);

        if (search != null) {
            search.updateTime();
        } else {
            searchRepository.save(Search.builder()
                    .memberId(memberId)
                    .keyword(keyword)
                    .searchType(searchType)
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public SearchResponse getSearchByType(final Long memberId, final SearchType searchType) {
        final List<Search> searchList = searchRepository.findTop5ByMemberIdAndSearchTypeOrderByUpdatedAtDesc(memberId, searchType);
        return SearchResponse.of(
                searchList.stream()
                        .map(search -> KeywordResponse.of(search.getId(), search.getKeyword()))
                        .toList()
        );
    }
}
