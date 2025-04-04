package com.cocos.cocos.db.hospital.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hospital")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "review_count", nullable = false)
    private int reviewCount = 0;

    @Column(name = "town_id", nullable = false)
    private Long townId;

    @Builder
    public Hospital(final String name, final String address, final String image, final Double latitude, final Double longitude, final int reviewCount, final Long townId) {
        this.name = name;
        this.address = address;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reviewCount = reviewCount;
        this.townId = townId;
    }

    public void addReview() {
        this.reviewCount++;
    }

    public void deleteReview() {
        this.reviewCount--;
    }
}
