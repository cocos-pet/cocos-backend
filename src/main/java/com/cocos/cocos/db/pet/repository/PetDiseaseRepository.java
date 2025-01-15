package com.cocos.cocos.db.pet.repository;

import com.cocos.cocos.db.pet.entity.PetDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetDiseaseRepository extends JpaRepository<PetDisease, Long> {

    boolean existsByPetId(final Long petId);

    List<PetDisease> findAllByPetId(final Long petId);
}
