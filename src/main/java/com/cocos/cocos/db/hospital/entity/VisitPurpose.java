package com.cocos.cocos.db.hospital.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "visit_purpose")
public class VisitPurpose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "label", nullable = false)
    private String name;

    @Builder
    public VisitPurpose(final String name) {
        this.name = name;
    }
}
