package com.cocos.cocos.db.review.db;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "review_summary",
        indexes = {
                @Index(name = "idx_review_summary_option_id_review_id", columnList = "review_summary_option_id, review_id")
        }
)
public class ReviewSummary extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "review_summary_option_id", nullable = false)
    private Long reviewSummaryOptionId;

    @Builder
    public ReviewSummary(final Long reviewId, final Long reviewSummaryOptionId) {
        this.reviewId = reviewId;
        this.reviewSummaryOptionId = reviewSummaryOptionId;
    }
}
