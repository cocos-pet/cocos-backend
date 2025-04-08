package com.cocos.cocos.db.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_address")
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(name = "town_id", nullable = false)
    private Long townId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Builder
    public MemberAddress(final Long memberId, final String address, final String roadAddress, final Long townId, final Double latitude, final Double longitude) {
        this.memberId = memberId;
        this.address = address;
        this.roadAddress = roadAddress;
        this.townId = townId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateAddress(final String address, final String roadAddress, final Long townId, final Double latitude, final Double longitude) {
        this.address = address;
        this.roadAddress = roadAddress;
        this.townId = townId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static MemberAddress createDefaultMemberAddress(final Long memberId, final String address, final String roadAddress, final Long townId, final Double latitude, final Double longitude) {
        return MemberAddress.builder()
                .memberId(memberId)
                .address(address)
                .roadAddress(roadAddress)
                .latitude(latitude)
                .longitude(longitude)
                .townId(townId)
                .build();
    }
}
