package com.cocos.cocos.db.subcomment.repository;

import com.cocos.cocos.db.subcomment.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
}
