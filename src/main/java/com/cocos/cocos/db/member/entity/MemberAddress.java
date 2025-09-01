package com.cocos.cocos.db.member.entity;

import com.cocos.cocos.enums.location.LocationType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name = "member_address")
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false, unique = true)
    private Long memberId;

    @Column(name = "address", nullable = false)
    @ColumnDefault("'주소를 설정해주세요!'")
    private String address;

    @Column(name = "road_address", nullable = false)
    @ColumnDefault("'도로명 주소를 설정해주세요!'")
    private String roadAddress;

    @Column(name = "location_id", nullable = false)
    @ColumnDefault("143")
    private Long locationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    @ColumnDefault("'DISTRICT'")
    private LocationType locationType;

    @Column(name = "latitude", nullable = false)
    @ColumnDefault("0.0")
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    @ColumnDefault("0.0")
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
                .build();
    }
}
