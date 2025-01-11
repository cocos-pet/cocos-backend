package com.cocos.cocos.db.breed.repository;

import com.cocos.cocos.db.breed.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreedRepository extends JpaRepository<Breed, Long> {
}
