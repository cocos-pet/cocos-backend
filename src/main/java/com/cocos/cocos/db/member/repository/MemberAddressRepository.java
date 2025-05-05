package com.cocos.cocos.db.member.repository;

import com.cocos.cocos.db.member.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    Optional<MemberAddress> findByMemberId(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);
}
