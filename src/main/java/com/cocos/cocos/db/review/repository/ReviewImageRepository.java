package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findAllByReviewIdIn(final List<Long> reviewIds);
}
