package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {

    List<PostCategory> findAllByIsAdminOnlyFalse();
}
