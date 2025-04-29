package com.cocos.cocos.db.comment.repository;

import com.cocos.cocos.db.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostId(final Long postId);

    int countByPostId(final Long postId);

    void deleteAllByPostId(final Long postId);

    List<Comment> findByPostIdOrderByCreatedAtAsc(@Param("postId") Long postId);

    List<Comment> findAllByMemberIdOrderByCreatedAtDesc(final Long memberId);

    List<Comment> findAllByPostIdIn(final List<Long> postIds);

    void deleteAllByPostIdInOrMemberId(final List<Long> postIds, final Long memberId);

    List<Comment> findAllByPostIdInOrMemberId(final List<Long> postIds, final Long memberId);
}
