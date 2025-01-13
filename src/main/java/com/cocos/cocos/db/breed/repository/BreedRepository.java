package com.cocos.cocos.db.breed.repository;

import com.cocos.cocos.db.breed.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {

    List<Breed> findAllByNameContainingAndAnimalId(final String Name, final Long animalId);
}
