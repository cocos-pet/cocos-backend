package com.cocos.cocos.db.member.entity;

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

    public static MemberAddress createDefaultMemberAddress(final Long memberId, final String address, final String roadAddress, final Long locationId, final Double latitude, final Double longitude, final LocationType locationType) {
        return MemberAddress.builder()
                .memberId(memberId)
                .address(address)
                .roadAddress(roadAddress)
                .latitude(latitude)
                .longitude(longitude)
                .locationId(locationId)
                .locationType(locationType)
                .build();
    }
}
