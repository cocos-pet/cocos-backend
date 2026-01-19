package com.cocos.cocos.api.notification.service;

import com.cocos.cocos.api.notification.dto.response.UnreadNotificationResponse;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.db.notification.repository.NotificationRepository;
import com.cocos.cocos.event.MagazinePublishedEvent;
import com.cocos.cocos.event.PostCommentEvent;
import com.cocos.cocos.event.PostLikeMilestoneEvent;
import com.cocos.cocos.event.PostSubCommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

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
    public UnreadNotificationResponse hasUnreadNotification(Long notifierId) {
        boolean hasUnread =
                notificationRepository.existsByNotifierIdAndIsReadFalse(notifierId);

        return new UnreadNotificationResponse(hasUnread);
    }

    private boolean isAlreadyNotified(final Long postId, final int likeCount) {
        return notificationRepository.existsByPostIdAndMilestone(postId, likeCount);
    }
}
