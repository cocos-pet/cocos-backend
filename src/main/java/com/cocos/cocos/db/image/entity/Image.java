package com.cocos.cocos.db.image.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "image")
public class Image extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "image", nullable = false)
    private String image;
}
