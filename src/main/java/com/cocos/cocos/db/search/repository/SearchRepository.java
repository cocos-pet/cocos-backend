package com.cocos.cocos.db.search.repository;

import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.enums.search.SearchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

    boolean existsByMemberIdAndKeywordAndSearchType(final Long memberId, final String keyword, final SearchType searchType);

    Search findByMemberIdAndKeywordAndSearchType(final Long memberId, final String keyword, final SearchType searchType);

    List<Search> findTop5ByMemberIdAndSearchTypeOrderByUpdatedAtDesc(final Long memberId, final SearchType searchType);
}
