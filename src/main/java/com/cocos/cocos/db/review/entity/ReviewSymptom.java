package com.cocos.cocos.db.review.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "review_symptom",
        indexes = {
                @Index(name = "idx_symptom_id_review_id", columnList = "symptom_id, review_id")
        }
)
public class ReviewSymptom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "symptom_id", nullable = false)
    private Long symptomId;

    @Builder
    public ReviewSymptom(final Long reviewId, final Long symptomId) {
        this.reviewId = reviewId;
        this.symptomId = symptomId;
    }
}
