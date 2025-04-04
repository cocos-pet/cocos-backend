package com.cocos.cocos.db.search.repository;

import com.cocos.cocos.db.search.entity.Search;
import com.cocos.cocos.enums.search.SearchType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

    //TODO: 안정적인 상황 확인 후 동시성 제어 제거
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Search findWithLockByMemberIdAndKeywordAndSearchType(final Long memberId, final String keyword, final SearchType searchType);

    List<Search> findTop5ByMemberIdAndSearchTypeOrderByUpdatedAtDesc(final Long memberId, final SearchType searchType);

    List<Search> findAllByMemberIdAndKeywordAndSearchType(final Long memberId, final String keyword, final SearchType searchType);
}
