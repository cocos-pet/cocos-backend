package com.cocos.cocos.db.member.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member_hospital")
public class MemberHospital extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Builder
    public MemberHospital(final Long hospitalId, final Long memberId) {
        this.hospitalId = hospitalId;
        this.memberId = memberId;
    }

    public void updateHospitalId(Long hospitalId) {
        if (Objects.equals(this.hospitalId, hospitalId)) return;
        this.hospitalId = hospitalId;
    }
}
