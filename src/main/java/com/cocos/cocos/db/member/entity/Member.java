package com.cocos.cocos.db.member.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.member.Platform;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
@Table(name = "member")
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "email")
    private String email;

    @Column(name = "image", nullable = false)
    @ColumnDefault("'member/baseProfileImage.png'")
    private String image;

    @Column(name = "platform", nullable = false)
    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(name = "sub", nullable = false)
    private String sub;

    @Column(name = "is_admin", nullable = false)
    @ColumnDefault("false")
    private boolean isAdmin;

    @Column(name = "my_hospital_id")
    private Long myHospitalId;

    @Column(name = "is_review_terms_agree", nullable = false)
    @ColumnDefault("false")
    private boolean isReviewTermsAgree;

    @Column(name = "review_terms_agree_at", nullable = true)
    private LocalDateTime reviewTermsAgreeAt;

    @Builder
    public Member(final String nickname, final String email, final String image, final Platform platform, final String sub, final boolean isAdmin, final Long myHospitalId) {
        this.nickname = nickname;
        this.email = email;
        this.image = image;
        this.platform = platform;
        this.sub = sub;
        this.isAdmin = isAdmin;
        this.myHospitalId = myHospitalId;
    }

    public static Member createDefaultMember(final String sub, final Platform platform) {
        return Member.builder()
                .platform(platform)
                .sub(sub)
                .build();
    }

    public void updateNickname(final String nickname) {
        if (nickname != null) this.nickname = nickname;
    }

    public void updateMyHospitalId(final Long myHospitalId) {
        if (myHospitalId != null) {
            this.myHospitalId = myHospitalId;
        }
    }

    public void updateReviewTermsAgree() {
        this.isReviewTermsAgree = true;
        this.reviewTermsAgreeAt = LocalDateTime.now();
    }
}
