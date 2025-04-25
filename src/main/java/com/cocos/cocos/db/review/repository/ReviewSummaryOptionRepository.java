package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewSummaryOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSummaryOptionRepository extends JpaRepository<ReviewSummaryOption, Long> {
}
