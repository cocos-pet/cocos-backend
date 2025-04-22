package com.cocos.cocos.db.hospital.repository;

import com.cocos.cocos.db.hospital.entity.HospitalTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalTagRepository extends JpaRepository<HospitalTag, Long> {
    List<HospitalTag> findAllByIdIn(final List<Long> hospitalIds);
}
