package com.cocos.cocos.db.disease.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "disease")
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "body_id", nullable = false)
    private Long bodyId;

    @Builder
    public Disease(String name, Long bodyId) {
        this.name = name;
        this.bodyId = bodyId;
    }
}
