package com.cocos.cocos.db.notification.entity;

import com.cocos.cocos.db.BaseTime;
import com.cocos.cocos.enums.notification.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_notification_user_created", columnList = "notifier_id, created_at DESC"),
                @Index(name = "idx_notification_user_read", columnList = "notifier_id, is_read"),
                @Index(name = "idx_notification_user_type_created", columnList = "notifier_id, notification_type, created_at DESC")
        }
)
public class Notification extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "notifier_id", nullable = false)
    private Long notifierId;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Column(name = "actor_nickname")
    private String actorNickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "notification_target_id", nullable = false)
    private Long notificationTargetId;

    @Column(name = "milestone")
    private Integer milestone;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content",  columnDefinition = "text")
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Builder
    public Notification(Long notifierId, Long actorId, String actorNickname, NotificationType notificationType, Long notificationTargetId, Long postId, String title, String content, boolean isRead, Integer milestone) {
        this.notifierId = notifierId;
        this.actorId = actorId;
        this.actorNickname = actorNickname;
        this.notificationType = notificationType;
        this.notificationTargetId = notificationTargetId;
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.milestone = milestone;
    }

    public static Notification postLikeMilestone(
            Long postId,
            Long postOwnerId,
            String postTitle,
            Long actorId,
            String actorNickname,
            int likeCount
    ) {
        return Notification.builder()
                .notifierId(postOwnerId)
                .actorId(actorId)
                .actorNickname(actorNickname)
                .notificationType(NotificationType.POST_LIKE_MILESTONE)
                .notificationTargetId(postId)
                .postId(postId)
                .title(postTitle)
                .milestone(likeCount)
                .build();
    }

    public void markRead() {
        this.isRead = true;
    }
}
