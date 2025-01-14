package com.cocos.cocos.db.post.entity;

import com.cocos.cocos.enums.tag.TagType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_tag")
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "tag_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TagType tagType;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Builder
    public PostTag(TagType tagType, Long tagId, Long postId) {
        this.tagType = tagType;
        this.tagId = tagId;
        this.postId = postId;
    }
}
