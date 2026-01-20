package com.cocos.cocos.api.notification.service;

import com.cocos.cocos.api.notification.dto.response.NotificationListResponse;
import com.cocos.cocos.api.notification.dto.response.NotificationResponse;
import com.cocos.cocos.api.notification.dto.response.UnreadNotificationResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.db.notification.repository.NotificationRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.enums.notification.NotificationCategory;
import com.cocos.cocos.enums.notification.NotificationIcon;
import com.cocos.cocos.event.MagazinePublishedEvent;
import com.cocos.cocos.event.PostCommentEvent;
import com.cocos.cocos.event.PostLikeMilestoneEvent;
import com.cocos.cocos.event.PostSubCommentEvent;
import com.cocos.cocos.external.AppDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final AppDataS3Client appDataS3Client;

    private static final int DEFAULT_PAGE_SIZE = 20;

    public void createForComment(final PostCommentEvent postCommentEvent) {
        final Notification notification = Notification.comment(postCommentEvent);
        notificationRepository.save(notification);
    }

    public void createForSubComment(final PostSubCommentEvent event) {
        final Notification notification = Notification.subComment(event);
        notificationRepository.save(notification);
    }

    public void createForMagazine(final MagazinePublishedEvent magazinePublishedEvent) {
        final List<Long> memberIds = memberRepository.findAllIds();
        final List<Notification> notifications = memberIds.stream()
                .map(memberId -> Notification.magazinePublished(memberId, magazinePublishedEvent))
                .toList();
        notificationRepository.saveAll(notifications);
    }

    public void createForPostLike(final PostLikeMilestoneEvent postLikeMilestoneEvent) {
        if (isAlreadyNotified(postLikeMilestoneEvent.postId(), postLikeMilestoneEvent.likeCount())) {
            return;
        }

        final Notification notification = Notification.postLikeMilestone(postLikeMilestoneEvent);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public UnreadNotificationResponse hasUnreadNotification(final Long notifierId) {
        boolean hasUnread =
                notificationRepository.existsByNotifierIdAndIsReadFalse(notifierId);

        return new UnreadNotificationResponse(hasUnread);
    }

    public void readNotification(final Long memberId, final Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_NOTIFICATION));
        notification.validateOwner(memberId);

        if (!notification.isRead()) {
            notification.markRead();
        }
    }


    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(
            final Long memberId,
            final NotificationCategory category,
            final LocalDateTime cursorCreatedAt,
            final Long cursorId
    ) {

        final List<Notification> notifications =
                notificationRepository.findNotifications(
                        memberId,
                        category.getNotificationTypes(),
                        cursorCreatedAt,
                        cursorId,
                        DEFAULT_PAGE_SIZE
                );

        if (notifications.isEmpty()) {
            return NotificationListResponse.of(null, null, List.of());
        }

        final List<NotificationResponse> responseList = notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getNotificationType(),
                        notification.isRead(),
                        notification.getCreatedAt(),

                        notification.getPostId(),
                        notification.getNotificationTargetId(),

                        notification.getTitle(),
                        notification.getContent(),
                        notification.getActorNickname(),

                        notification.getMilestone(),

                        appDataS3Client.getPresignedUrl(NotificationIcon.imageKeyOf(notification.getNotificationType()))
                )).toList();

        final Notification last = notifications.getLast();

        return NotificationListResponse.of(
                last.getCreatedAt(),
                last.getId(),
                responseList
        );
    }

    private boolean isAlreadyNotified(final Long postId, final int likeCount) {
        return notificationRepository.existsByPostIdAndMilestone(postId, likeCount);
    }
}
