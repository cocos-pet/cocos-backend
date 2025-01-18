package com.cocos.cocos.db.member.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_token")
public class MemberToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "kakao_refresh_token", nullable = false)
    private String KakaoRefreshToken;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Builder
    public MemberToken(String refreshToken, String kakaoRefreshToken, Long memberId) {
        this.refreshToken = refreshToken;
        KakaoRefreshToken = kakaoRefreshToken;
        this.memberId = memberId;
    }

    public void updateRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
