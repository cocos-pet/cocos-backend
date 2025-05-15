package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewSummaryOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewSummaryOptionRepository extends JpaRepository<ReviewSummaryOption, Long> {
    long countByIdInAndIsGoodTrue(final List<Long> ids);

    long countByIdInAndIsGoodFalse(final List<Long> ids);
}
