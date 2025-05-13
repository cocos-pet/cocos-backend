package com.cocos.cocos.db.animal.repository;

import com.cocos.cocos.db.animal.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    long countByIdIn(final List<Long> ids);
}
