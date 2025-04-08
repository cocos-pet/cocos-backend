package com.cocos.cocos.db.district.repository;

import com.cocos.cocos.db.district.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findByCityId(final Long cityId);

    District findByNameAndCityId(final String name, final Long cityId);
}
