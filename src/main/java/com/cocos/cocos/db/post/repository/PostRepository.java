package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @Query("SELECT p FROM Post p ORDER BY p.likeCount DESC")
    List<Post> findTop5ByLikeCountDesc();

    @Query("SELECT p FROM Post p WHERE p.id IN :postIds ORDER BY p.likeCount DESC")
    List<Post> findTopPostsByPostIds(@Param("postIds") List<Long> postIds, Pageable pageable);

    List<Post> findAllByMemberId(final Long memberId);
}
