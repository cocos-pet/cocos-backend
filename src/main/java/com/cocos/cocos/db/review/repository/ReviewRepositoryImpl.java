package com.cocos.cocos.db.review.repository;

import com.cocos.cocos.api.review.dto.query.ReviewSearchCondition;
import com.cocos.cocos.db.review.db.Review;
import com.cocos.cocos.enums.location.LocationType;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cocos.cocos.db.district.entity.QDistrict.district;
import static com.cocos.cocos.db.hospital.entity.QHospital.hospital;
import static com.cocos.cocos.db.review.db.QReview.review;
import static com.cocos.cocos.db.review.db.QReviewSummary.reviewSummary;
import static com.cocos.cocos.db.review.db.QReviewSymptom.reviewSymptom;
import static com.cocos.cocos.db.symptom.entity.QSymptom.symptom;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Review> findBySearchCondition(final ReviewSearchCondition condition) {

        final List<Long> filteredIds = getFilteredReviewIds(condition);
        if (filteredIds != null && filteredIds.isEmpty()) {
            return List.of();
        }

        final JPQLQuery<Review> query = jpaQueryFactory.selectFrom(review);

        addLocationCondition(query, condition);
        addHospitalCondition(query, condition);
        addFilteredIdsCondition(query, filteredIds);
        addCursorCondition(query, condition);

        return query
                .orderBy(review.id.desc())
                .limit(condition.size())
                .fetch();
    }

    private List<Long> getFilteredReviewIds(final ReviewSearchCondition condition) {

        List<Long> ids = null;

        if (condition.bodyId() != null) {
            final List<Long> symptomIds = jpaQueryFactory
                    .select(symptom.id)
                    .from(symptom)
                    .where(symptom.bodyId.eq(condition.bodyId()))
                    .fetch();

            if (symptomIds.isEmpty()) return List.of();

            ids = jpaQueryFactory
                    .select(reviewSymptom.reviewId)
                    .from(reviewSymptom)
                    .where(reviewSymptom.symptomId.in(symptomIds))
                    .fetch();
        }

        if (condition.summaryOptionId() != null) {
            final List<Long> summaryIds = jpaQueryFactory
                    .select(reviewSummary.reviewId)
                    .from(reviewSummary)
                    .where(reviewSummary.reviewSummaryOptionId.eq(condition.summaryOptionId()))
                    .fetch();

            if (summaryIds.isEmpty()) {
                return List.of();
            }

            if (ids == null) {
                return summaryIds;
            }

            ids.retainAll(summaryIds);
        }

        return ids;
    }

    private void addLocationCondition(
            final JPQLQuery<?> query,
            final ReviewSearchCondition condition
    ) {
        if (condition.locationId() == null || condition.locationType() == null) return;

        query.join(hospital).on(review.hospitalId.eq(hospital.id))
                .join(district).on(hospital.districtId.eq(district.id));

        if (condition.locationType() == LocationType.CITY) {
            query.where(district.cityId.eq(condition.locationId()));
        } else {
            query.where(hospital.districtId.eq(condition.locationId()));
        }
    }

    private void addHospitalCondition(final JPQLQuery<?> query, final ReviewSearchCondition condition) {
        if (condition.hospitalId() != null) {
            query.where(review.hospitalId.eq(condition.hospitalId()));
        }
    }

    private void addFilteredIdsCondition(final JPQLQuery<?> query, final List<Long> ids) {
        if (ids != null) {
            query.where(review.id.in(ids));
        }
    }

    private void addCursorCondition(final JPQLQuery<?> query, final ReviewSearchCondition condition) {
        if (condition.cursorId() != null) {
            query.where(review.id.lt(condition.cursorId()));
        }
    }
}
