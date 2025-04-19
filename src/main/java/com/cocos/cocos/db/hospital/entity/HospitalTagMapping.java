package com.cocos.cocos.db.hospital.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hospital_tag_mapping")
public class HospitalTagMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "hospital_tag_id", nullable = false)
    private Long hospitalTagId;

    @Builder
    public HospitalTagMapping(final Long hospitalId, final Long hospitalTagId) {
        this.hospitalId = hospitalId;
        this.hospitalTagId = hospitalTagId;
    }
}
