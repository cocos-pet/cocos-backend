package com.cocos.cocos.db.comment.repository;

import com.cocos.cocos.db.comment.entity.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCommentRepository extends JpaRepository<SubComment, Long> {

    int countByCommentId(final Long commentId);

    void deleteAllByCommentId(final Long commentId);

    List<SubComment> findByCommentIdInOrderByCreatedAtAsc(@Param("commentId") List<Long> commentId);

    List<SubComment> findByMemberIdOrderByCreatedAtAsc(final Long memberId);
}
