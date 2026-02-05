package com.cocos.cocos.db.review.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review_image")
public class ReviewImage extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Builder
    public ReviewImage(final String image, final Long reviewId) {
        this.image = image;
        this.reviewId = reviewId;
    }
}
