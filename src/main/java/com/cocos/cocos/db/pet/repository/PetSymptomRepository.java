package com.cocos.cocos.db.pet.repository;

import com.cocos.cocos.db.pet.entity.PetSymptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetSymptomRepository extends JpaRepository<PetSymptom, Long> {
}
