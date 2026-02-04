package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.entity.ReviewSymptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewSymptomRepository extends JpaRepository<ReviewSymptom, Long> {

    void deleteAllByReviewId(final Long reviewId);

    void deleteAllByReviewIdIn(final List<Long> reviewIds);

    List<ReviewSymptom> findByReviewIdIn(final List<Long> reviewIds);
}
