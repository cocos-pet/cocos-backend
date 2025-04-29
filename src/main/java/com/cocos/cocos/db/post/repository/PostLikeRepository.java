package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    void deleteByMemberIdAndPostId(final Long memberId, final Long postId);

    boolean existsByMemberIdAndPostId(final Long memberId, final Long postId);

    int countByPostId(final Long postId);

    void deleteAllByPostId(final Long postId);

    List<PostLike> findAllByMemberId(final Long memberId);
}
