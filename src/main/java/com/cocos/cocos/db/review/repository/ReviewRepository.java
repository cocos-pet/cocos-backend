package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByHospitalId(final Long hospitalId);

    List<Review> findAllByMemberId(final Long memberId);

    void deleteAllByIdIn(final List<Long> reviewIds);

    List<Review> findAllByMemberIdAndIdLessThan(final Long memberId, final Long cursorId, final Pageable pageable);

    List<Review> findAllByMemberId(final Long memberId, final Pageable pageable);
}
