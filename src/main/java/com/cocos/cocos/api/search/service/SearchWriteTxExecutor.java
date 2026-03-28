package com.cocos.cocos.api.search.service;

import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.db.search.repository.SearchRepository;
import com.cocos.cocos.enums.search.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SearchWriteTxExecutor {

    private final SearchRepository searchRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdateExisting(final Long memberId, final String keyword, final SearchType searchType) {
        final Search existingSearch = searchRepository.findByMemberIdAndKeywordAndSearchType(memberId, keyword, searchType);
        if (existingSearch != null) {
            existingSearch.updateTime();
            return;
        }

        searchRepository.save(Search.builder()
                .memberId(memberId)
                .keyword(keyword)
                .searchType(searchType)
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean recoverDuplicate(final Long memberId, final String keyword, final SearchType searchType) {
        final Search existingSearch = searchRepository.findByMemberIdAndKeywordAndSearchType(memberId, keyword, searchType);
        if (existingSearch == null) {
            return false;
        }

        existingSearch.updateTime();
        return true;
    }
}
