package com.cocos.cocos.db.comment.repository;

import com.cocos.cocos.db.comment.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Long> {

    int countByCommentId(final Long commentId);

    void deleteAllByCommentId(final Long commentId);
}
