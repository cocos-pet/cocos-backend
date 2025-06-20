package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.db.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByHospitalId(final Long hospitalId);

    List<Review> findAllByMemberId(final Long memberId);

    void deleteAllByIdIn(final List<Long> reviewIds);

    List<Review> findAllByMemberIdAndIdLessThan(final Long memberId, final Long cursorId, final Pageable pageable);

    List<Review> findAllByMemberId(final Long memberId, final Pageable pageable);

    @Query("""
                SELECT DISTINCT r FROM Review r
                JOIN ReviewSummary s ON r.id = s.reviewId
                WHERE r.hospitalId = :hospitalId
                AND (:summaryOptionId IS NULL OR s.reviewSummaryOptionId = :summaryOptionId)
                AND (:cursorId IS NULL OR r.id < :cursorId)
            """)
    List<Review> findByHospitalIdAndSummaryOptionIdAndCursorId(
            final Long hospitalId, final Long summaryOptionId, final Long cursorId, final Pageable pageable
    );

    @Query("""
                SELECT DISTINCT r FROM Review r
                JOIN ReviewSummary s ON r.id = s.reviewId
                WHERE (:reviewIds IS NULL OR r.id IN :reviewIds)
                AND (:summaryOptionId IS NULL OR s.reviewSummaryOptionId = :summaryOptionId)
                AND (:cursorId IS NULL OR r.id < :cursorId)
                AND (r.hospitalId IN :hospitalIds)
            """)
    List<Review> findByBodyAndLocationAndSummaryOptionIdAndCursorId(
            final List<Long> reviewIds, final List<Long> hospitalIds, final Long summaryOptionId, final Long cursorId, final Pageable pageable
    );
}
