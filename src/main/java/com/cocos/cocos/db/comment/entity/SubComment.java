package com.cocos.cocos.db.comment.entity;

import com.cocos.cocos.db.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sub_comment")
public class SubComment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    @Column(name = "mentioned_member_id", nullable = false)
    private Long mentionedMemberId;

    @Builder
    public SubComment(String content, Long memberId, Long commentId, Long mentionedMemberId) {
        this.content = content;
        this.memberId = memberId;
        this.commentId = commentId;
        this.mentionedMemberId = mentionedMemberId;
    }

    public boolean isSelfSubComment(final Long parentCommentMemberId) {
        return memberId.equals(parentCommentMemberId);
    }

    public boolean isSelfComment(final Long postMemberId) {
        return memberId.equals(postMemberId);
    }
}
