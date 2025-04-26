package com.cocos.cocos.db.hospital.repository;

import com.cocos.cocos.db.hospital.entity.HospitalVisitPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalVisitPurposeRepository extends JpaRepository<HospitalVisitPurpose, Long> {
}
