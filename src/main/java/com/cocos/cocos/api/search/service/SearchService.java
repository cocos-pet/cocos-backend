package com.cocos.cocos.api.search.service;

import com.cocos.cocos.api.search.dto.response.KeywordResponse;
import com.cocos.cocos.api.search.dto.response.SearchResponse;
import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.db.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository searchRepository;

    @Transactional(readOnly = true)
    public SearchResponse getSearch(final Long memberId) {
        final List<Search> searchList = searchRepository.findTop5ByMemberIdOrderByUpdatedAtDesc(memberId);
        return SearchResponse.of(
                searchList.stream()
                        .map(search -> KeywordResponse.of(search.getId(), search.getKeyword()))
                        .toList()
        );
    }

    @Transactional
    public Void addSearch(final Long memberId, final String keyword) {
        if (searchRepository.existsByMemberIdAndKeyword(memberId, keyword)) {
            final Search search = searchRepository.findByMemberIdAndKeyword(memberId, keyword);
            search.updateTime();
        } else {
            searchRepository.save(Search.builder().memberId(memberId).keyword(keyword).build());
        }
        return null;
    }
}
