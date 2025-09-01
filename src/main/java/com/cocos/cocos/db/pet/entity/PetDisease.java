package com.cocos.cocos.db.pet.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pet_disease",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"pet_id", "disease_id"})
        })
public class PetDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "disease_id", nullable = false)
    private Long diseaseId;

    @Builder
    public PetDisease(Long petId, Long diseaseId) {
        this.petId = petId;
        this.diseaseId = diseaseId;
    }
}
