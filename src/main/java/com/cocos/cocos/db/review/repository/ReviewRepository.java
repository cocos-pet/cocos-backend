package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.db.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    List<Review> findAllByHospitalId(final Long hospitalId);

    List<Review> findAllByMemberId(final Long memberId);

    void deleteAllByIdIn(final List<Long> reviewIds);

    List<Review> findAllByMemberIdAndIdLessThan(final Long memberId, final Long cursorId, final Pageable pageable);

    List<Review> findAllByMemberId(final Long memberId, final Pageable pageable);

    Optional<Review> findTopByMemberIdAndDiseaseIdIsNotNullOrderByVisitedAtDesc(final Long memberId);
}
