package com.cocos.cocos.db.hospital.repository;

import com.cocos.cocos.db.hospital.entity.HospitalTag;
import com.cocos.cocos.db.hospital.entity.HospitalTagMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalTagMappingRepository extends JpaRepository<HospitalTagMapping, Long> {
    List<HospitalTagMapping> findAllByHospitalId(final Long hospitalId);

}
