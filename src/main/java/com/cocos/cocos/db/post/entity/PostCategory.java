package com.cocos.cocos.db.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_category")
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "is_admin_only", nullable = false)
    private boolean isAdminOnly;

    @Builder
    public PostCategory(String name, String image, boolean isAdminOnly) {
        this.name = name;
        this.image = image;
        this.isAdminOnly = isAdminOnly;
    }
}
