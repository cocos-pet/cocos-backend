package com.cocos.cocos.db.member.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.location.LocationType;
import com.cocos.cocos.enums.member.Platform;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "platform", nullable = false)
    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(name = "sub", nullable = false)
    private String sub;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "current_address", nullable = false)
    private String currentAddress;

    @Column(name = "town_id", nullable = false)
    private Long townId;

    @Column(name = "location_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    @Builder
    public Member(final String nickname, final String email, final String image, final Platform platform, final String sub, final boolean isAdmin, final String currentAddress, final Long townId, final LocationType locationType) {
        this.nickname = nickname;
        this.email = email;
        this.image = image;
        this.platform = platform;
        this.sub = sub;
        this.isAdmin = isAdmin;
        this.currentAddress = currentAddress;
        this.townId = townId;
        this.locationType = locationType;
    }

    public void updateNickname(final String nickname) {
        if (nickname != null) this.nickname = nickname;
    }

    public void updateLocation(final LocationType locationType, final Long townId) {
        this.locationType = locationType;
        this.townId = townId;
    }
}
