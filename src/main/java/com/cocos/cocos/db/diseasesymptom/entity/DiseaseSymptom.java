package com.cocos.cocos.db.diseasesymptom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "disease_symptom")
public class DiseaseSymptom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "disease_id", nullable = false)
    private Long diseaseId;

    @Column(name = "symptom_id", nullable = false)
    private Long symptomId;
}
