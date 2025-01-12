package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    void deleteByMemberIdAndPostId(final Long memberId, final Long postId);
}
