package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
