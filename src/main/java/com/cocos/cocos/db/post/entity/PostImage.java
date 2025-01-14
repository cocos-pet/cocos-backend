package com.cocos.cocos.db.post.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_image")
public class PostImage extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "image", nullable = false)
    private String image;

    @Builder
    public PostImage(Long postId, String image) {
        this.postId = postId;
        this.image = image;
    }
}
