package com.cocos.cocos.db.post.repository;

import com.cocos.cocos.db.post.entity.PostTag;
import com.cocos.cocos.enums.tag.TagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    List<PostTag> findAllByPostId(final Long postId);

    void deleteAllByPostId(final Long postId);

    @Query("SELECT pt FROM PostTag pt WHERE pt.tagId IN :tagIds AND pt.tagType = :tagType")
    List<PostTag> findAllByTagIdAndTagType(@Param("tagIds") List<Long> tagIds, @Param("tagType") TagType tagType);

    void deleteAllByPostIdIn(final List<Long> postIds);
}
