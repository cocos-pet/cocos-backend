package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewSymptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSymptomRepository extends JpaRepository<ReviewSymptom, Long> {

    void deleteAllByReviewId(final Long reviewId);
}
