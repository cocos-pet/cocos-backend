package com.cocos.cocos.db.memberhospital.repository;

import com.cocos.cocos.db.memberhospital.entity.MemberHospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberHospitalRepository extends JpaRepository<MemberHospital, Long> {
}
