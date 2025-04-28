package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewSummary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewSummaryRepository extends CrudRepository<ReviewSummary, Long> {
    int countByReviewIdInAndReviewSummaryOptionId(final List<Long> reviewIds, final Long reviewSummaryOptionId);
}
