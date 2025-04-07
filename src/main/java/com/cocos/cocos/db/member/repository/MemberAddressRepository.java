package com.cocos.cocos.db.member.repository;

import com.cocos.cocos.db.member.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    MemberAddress findByMemberId(final Long memberId);
}
