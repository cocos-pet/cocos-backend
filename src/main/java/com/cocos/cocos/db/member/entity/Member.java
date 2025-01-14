package com.cocos.cocos.db.member.entity;

import com.cocos.cocos.db.BaseTime;
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

    @Column(name = "nickname", nullable = false, unique = true)
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

    @Builder
    public Member(String nickname, String email, String image, Platform platform, String sub, boolean isAdmin) {
        this.nickname = nickname;
        this.email = email;
        this.image = image;
        this.platform = platform;
        this.sub = sub;
        this.isAdmin = isAdmin;
    }
}
