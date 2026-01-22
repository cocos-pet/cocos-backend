package com.cocos.cocos.db.post.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
public class Post extends BaseTime {

    private static final Long MAGAZINE_CATEGORY_ID = 4L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Builder
    public Post(String title, String content, Long memberId, Long categoryId) {
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.categoryId = categoryId;
    }

    public boolean isMagazine() {
        return MAGAZINE_CATEGORY_ID.equals(categoryId);
    }

    public void deleteLike() {
        this.likeCount--;
    }
}
