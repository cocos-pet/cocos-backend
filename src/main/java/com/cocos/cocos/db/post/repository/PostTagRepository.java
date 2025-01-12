package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
