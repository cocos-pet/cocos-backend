package com.cocos.cocos.db.symptom.repository;

import com.cocos.cocos.db.symptom.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {
}
