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

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "road_address", nullable = true)
    private String roadAddress;

    @Column(name = "image", nullable = true)
    private String image;

    @Column(name = "latitude", nullable = true)
    private Double latitude;

    @Column(name = "longitude", nullable = true)
    private Double longitude;

    @Column(name = "review_count", nullable = false)
    private int reviewCount = 0;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Builder
    public Hospital(final String name, final String address, final String roadAddress, final String image, final Double latitude, final Double longitude, final int reviewCount, final Long districtId, final String phoneNumber, final String introduction) {
        this.name = name;
        this.address = address;
        this.roadAddress = roadAddress;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reviewCount = reviewCount;
        this.phoneNumber = phoneNumber;
        this.introduction = introduction;
        this.districtId = districtId;
    }

    public void addReview() {
        this.reviewCount++;
    }

    public void deleteReview() {
        this.reviewCount--;
    }

    public String getDisplayAddress() {
        return roadAddress != null ? roadAddress : address;
    }
}
