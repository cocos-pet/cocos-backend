package com.cocos.cocos.db.post.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "post_id"})
        })
public class PostLike extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Builder
    public PostLike(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }
}
