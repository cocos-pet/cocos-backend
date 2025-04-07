package com.cocos.cocos.db.town.repository;

import com.cocos.cocos.db.town.entity.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TownRepository extends JpaRepository<Town, Long> {
    Town findByNameAndDistrictId(final String name, final Long districtId);
}
