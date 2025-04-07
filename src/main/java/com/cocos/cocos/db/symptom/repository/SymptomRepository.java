package com.cocos.cocos.db.symptom.repository;

import com.cocos.cocos.db.symptom.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {

    List<Symptom> findAllByBodyId(final Long bodyId);

    long countByIdIn(final List<Long> ids);
}
