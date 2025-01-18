package com.cocos.cocos.db.member.repository;

import com.cocos.cocos.db.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByNickname(final String nickname);

    boolean existsBySub(final String sub);

    Member findBySub(final String sub);
}
