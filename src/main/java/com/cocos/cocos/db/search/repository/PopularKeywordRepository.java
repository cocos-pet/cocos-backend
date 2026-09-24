package com.cocos.cocos.db.search.repository;

import com.cocos.cocos.db.search.entity.PopularKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularKeywordRepository extends JpaRepository<PopularKeyword, Long> {

    List<PopularKeyword> findTop5ByOrderByDisplayRankAsc();
}
