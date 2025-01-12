package com.cocos.cocos.db.disease.repository;

import com.cocos.cocos.db.disease.entity.DiseaseSymptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiseaseSymptomRepository extends JpaRepository<DiseaseSymptom, Long> {
}
