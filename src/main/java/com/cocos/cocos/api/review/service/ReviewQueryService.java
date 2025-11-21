package com.cocos.cocos.api.review.service;

import com.cocos.cocos.api.review.dto.query.ReviewSearchCondition;
import com.cocos.cocos.db.district.entity.QDistrict;
import com.cocos.cocos.db.hospital.entity.QHospital;
import com.cocos.cocos.db.review.db.QReview;
import com.cocos.cocos.db.review.db.QReviewSummary;
import com.cocos.cocos.db.review.db.QReviewSymptom;
import com.cocos.cocos.db.review.db.Review;
import com.cocos.cocos.db.symptom.entity.QSymptom;
import com.cocos.cocos.enums.location.LocationType;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Review> findReviews(final ReviewSearchCondition condition) {
        final QReview r = QReview.review;
        final QHospital h = QHospital.hospital;
        final QDistrict d = QDistrict.district;

        final List<Long> filteredIds = getFilteredReviewIds(condition);
        if (filteredIds != null && filteredIds.isEmpty()) {
            return List.of();
        }

        final JPQLQuery<Review> query = jpaQueryFactory.selectFrom(r);

        addLocationCondition(query, condition, r, h, d);
        addHospitalCondition(query, condition, r);
        addFilteredIdsCondition(query, filteredIds, r);
        addCursorCondition(query, condition, r);

        return query
                .orderBy(r.id.desc())
                .limit(condition.getSize())
                .fetch();
    }

    private List<Long> getFilteredReviewIds(final ReviewSearchCondition condition) {
        final QReviewSymptom rsy = QReviewSymptom.reviewSymptom;
        final QReviewSummary rs = QReviewSummary.reviewSummary;
        final QSymptom s = QSymptom.symptom;

        List<Long> ids = null;

        if (condition.getBodyId() != null) {
            final List<Long> symptomIds = jpaQueryFactory
                    .select(s.id)
                    .from(s)
                    .where(s.bodyId.eq(condition.getBodyId()))
                    .fetch();

            if (symptomIds.isEmpty()) return List.of();

            ids = jpaQueryFactory
                    .select(rsy.reviewId)
                    .from(rsy)
                    .where(rsy.symptomId.in(symptomIds))
                    .fetch();
        }

        if (condition.getSummaryOptionId() != null) {
            final List<Long> summaryIds = jpaQueryFactory
                    .select(rs.reviewId)
                    .from(rs)
                    .where(rs.reviewSummaryOptionId.eq(condition.getSummaryOptionId()))
                    .fetch();

            if (summaryIds.isEmpty()) return List.of();

            if (ids == null) return summaryIds;

            ids.retainAll(summaryIds);
        }

        return ids;
    }

    private void addLocationCondition(
            final JPQLQuery<?> query,
            final ReviewSearchCondition c,
            final QReview r,
            final QHospital h,
            final QDistrict d
    ) {
        if (c.getLocationId() == null || c.getLocationType() == null) return;

        query.join(h).on(r.hospitalId.eq(h.id))
                .join(d).on(h.districtId.eq(d.id));

        if (c.getLocationType() == LocationType.CITY) {
            query.where(d.cityId.eq(c.getLocationId()));
        } else {
            query.where(h.districtId.eq(c.getLocationId()));
        }
    }

    private void addHospitalCondition(final JPQLQuery<?> query, final ReviewSearchCondition condition, final QReview r) {
        if (condition.getHospitalId() != null) {
            query.where(r.hospitalId.eq(condition.getHospitalId()));
        }
    }

    private void addFilteredIdsCondition(final JPQLQuery<?> query, final List<Long> ids, final QReview r) {
        if (ids != null) {
            query.where(r.id.in(ids));
        }
    }

    private void addCursorCondition(final JPQLQuery<?> query, final ReviewSearchCondition condition, final QReview r) {
        if (condition.getCursorId() != null) {
            query.where(r.id.lt(condition.getCursorId()));
        }
    }
}
