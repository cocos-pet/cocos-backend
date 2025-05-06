package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReviewId(final Long reviewId);

    void deleteAllByReviewId(final Long reviewId);

    void deleteAllByReviewIdIn(final List<Long> reviewIds);

    List<ReviewImage> findAllByReviewIdIn(final List<Long> reviewIds);
}
