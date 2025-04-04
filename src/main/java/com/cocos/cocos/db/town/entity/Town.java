package com.cocos.cocos.db.town.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "town")
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "district_id", nullable = false)
    private Long districtId;

    @Builder
    public Town(final String name, final Long districtId) {
        this.name = name;
        this.districtId = districtId;
    }
}
