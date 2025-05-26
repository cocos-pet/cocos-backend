package com.cocos.cocos.db.breed.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "breed")
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "animal_id", nullable = false)
    private Long animalId;

    @Column(name = "is_etc", nullable = false)
    private boolean isEtc;

    @Builder
    public Breed(String name, Long animalId, boolean isEtc) {
        this.name = name;
        this.animalId = animalId;
        this.isEtc = isEtc;
    }
}
