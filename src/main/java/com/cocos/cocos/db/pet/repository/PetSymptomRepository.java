package com.cocos.cocos.db.pet.repository;

import com.cocos.cocos.db.pet.entity.PetSymptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetSymptomRepository extends JpaRepository<PetSymptom, Long> {

    boolean existsByPetId(final Long petId);

    List<PetSymptom> findAllByPetId(final Long petId);

    void deleteAllByPetId(final Long petId);
}
