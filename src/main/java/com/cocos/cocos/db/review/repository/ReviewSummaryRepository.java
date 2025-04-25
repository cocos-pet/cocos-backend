package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewSummary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSummaryRepository extends CrudRepository<ReviewSummary, Long> {
}
