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

    /**
     * 리뷰 검색 조건에 따라 리뷰 목록을 조회한다.
     *
     * <p>1) bodyId, summaryOptionId 조건은 JOIN 시 전체 row 수가 크게 증가하는 요인이므로
     *    먼저 reviewId만 선별해 가볍게 필터링한다. (쿼리 비용 최소화)</p>
     *
     * <p>2) cursorId 기반 페이지네이션을 적용하여
     *    review.id < cursorId 조건을 통해 안정적인 Keyset Pagination을 수행한다.</p>
     *
     * <p>3) Location 조건은 병원/지역 테이블을 JOIN하여 필터링하며,
     *    CITY와 DISTRICT의 계층 구조에 따라 조회 기준이 달라진다.</p>
     *
     * <p>4) bodyId + summaryOptionId가 함께 들어오면
     *    사전 필터링된 reviewId 목록의 교집합(retainAll)을 통해 AND 조건을 구현한다.</p>
     */
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
