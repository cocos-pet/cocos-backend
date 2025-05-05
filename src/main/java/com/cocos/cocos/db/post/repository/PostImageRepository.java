package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findAllByPostId(final Long postId);

    void deleteAllByPostId(final Long postId);

    void deleteAllByPostIdIn(final List<Long> postIds);
}
