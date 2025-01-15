package com.cocos.cocos.db.pet.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pet_symptom")
public class PetSymptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "symptom_id", nullable = false)
    private Long symptomId;

    @Builder
    public PetSymptom(Long petId, Long symptomId) {
        this.petId = petId;
        this.symptomId = symptomId;
    }
}
