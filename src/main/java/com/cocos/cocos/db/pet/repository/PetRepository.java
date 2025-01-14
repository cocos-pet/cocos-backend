package com.cocos.cocos.db.pet.repository;

import com.cocos.cocos.db.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Pet findByMemberId(final Long memberId);

    List<Pet> findAllByMemberIdIn(@Param("memberIds") List<Long> memberIds);

}
