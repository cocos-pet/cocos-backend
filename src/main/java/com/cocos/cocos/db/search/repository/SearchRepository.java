package com.cocos.cocos.db.search.repository;

import com.cocos.cocos.db.search.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

    List<Search> findTop5ByMemberIdOrderByCreatedAtDesc(final Long memberId);
}
