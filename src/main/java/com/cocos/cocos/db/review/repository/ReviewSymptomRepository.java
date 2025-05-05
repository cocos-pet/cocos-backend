package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewSymptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewSymptomRepository extends JpaRepository<ReviewSymptom, Long> {
    void deleteAllByReviewIdIn(final List<Long> reviewIds);
}
