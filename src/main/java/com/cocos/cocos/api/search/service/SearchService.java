package com.cocos.cocos.api.search.service;

import com.cocos.cocos.api.search.dto.response.KeywordResponse;
import com.cocos.cocos.api.search.dto.response.SearchResponse;
import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.enums.search.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;
    private final SearchWriteTxExecutor searchWriteTxExecutor;

    public void addSearch(final Long memberId, final String keyword, final SearchType searchType) {
        try {
            searchWriteTxExecutor.saveOrUpdateExisting(memberId, keyword, searchType);
        } catch (DuplicateKeyException exception) {
            final boolean recovered = searchWriteTxExecutor.recoverDuplicate(memberId, keyword, searchType);
            if (!recovered) {
                throw exception;
            }
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
