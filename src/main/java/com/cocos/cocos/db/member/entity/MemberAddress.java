package com.cocos.cocos.db.member.entity;

import com.cocos.cocos.api.member.constant.MemberDefaults;
import com.cocos.cocos.enums.location.LocationType;
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

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "locatoin_type", nullable = false)
    private LocationType locationType;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Builder
    public MemberAddress(final Long memberId, final String address, final String roadAddress, final Long locationId, final Double latitude, final Double longitude, final LocationType locationType) {
        this.memberId = memberId;
        this.address = address;
        this.roadAddress = roadAddress;
        this.locationId = locationId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationType = locationType;
    }

    public void updateAddress(final String address, final String roadAddress, final Long locationId, final Double latitude, final Double longitude, final LocationType locationType) {
        this.address = address;
        this.roadAddress = roadAddress;
        this.locationId = locationId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationType = locationType;
    }

    public static MemberAddress createDefaultMemberAddress(final Long memberId) {
        return MemberAddress.builder()
                .memberId(memberId)
                .address(MemberDefaults.DEFAULT_ADDRESS)
                .roadAddress(MemberDefaults.DEFAULT_ROAD_ADDRESS)
                .latitude(MemberDefaults.DEFAULT_LATITUDE)
                .longitude(MemberDefaults.DEFAULT_LONGITUDE)
                .locationId(MemberDefaults.DEFAULT_LOCATION_ID)
                .locationType(MemberDefaults.DEFAULT_LOCATION_TYPE)
                .build();
    }
}
