package com.cocos.cocos.db.review.db;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review_summary_option")
public class ReviewSummaryOption extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "is_good", nullable = false)
    private Boolean isGood;

    @Column(name = "label", nullable = false)
    private String label;

    @Builder
    public ReviewSummaryOption(final Boolean isGood, final String label) {
        this.isGood = isGood;
        this.label = label;
    }
}
