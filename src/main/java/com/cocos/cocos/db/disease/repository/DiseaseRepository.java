package com.cocos.cocos.db.disease.repository;

import com.cocos.cocos.db.disease.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {

    List<Disease> findAllByBodyId(final Long bodyId);
}
