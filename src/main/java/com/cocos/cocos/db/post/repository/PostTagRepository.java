package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findAllByPostId(final Long postId);

    void deleteAllByPostId(final Long postId);
}
