package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.api.review.dto.query.ReviewSearchCondition;
import com.cocos.cocos.db.review.db.Review;

import java.util.List;

public interface ReviewRepositoryCustom {
    List<Review> findBySearchCondition(final ReviewSearchCondition condition);
}
