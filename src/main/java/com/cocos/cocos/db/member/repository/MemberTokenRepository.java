package com.cocos.cocos.db.member.repository;

import com.cocos.cocos.db.member.entity.MemberToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {

    MemberToken findByMemberId(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    MemberToken findByRefreshToken(final String refreshToken);

    boolean existsByRefreshToken(final String refreshToken);
}
