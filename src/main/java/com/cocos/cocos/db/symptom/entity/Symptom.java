package com.cocos.cocos.db.symptom.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "symptom")
public class Symptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "body_id", nullable = false)
    private Long bodyId;

    @Builder
    public Symptom(String name, Long bodyId) {
        this.name = name;
        this.bodyId = bodyId;
    }
}
