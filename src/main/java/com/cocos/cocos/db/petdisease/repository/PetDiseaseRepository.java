package com.cocos.cocos.db.petdisease.repository;

import com.cocos.cocos.db.petdisease.entity.PetDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetDiseaseRepository extends JpaRepository<PetDisease, Long> {
}
