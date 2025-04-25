package com.cocos.cocos.db.review.db;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review_symptom")
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
