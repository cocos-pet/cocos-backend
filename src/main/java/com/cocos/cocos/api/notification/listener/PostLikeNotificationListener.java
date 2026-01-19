package com.cocos.cocos.api.notification.listener;

import com.cocos.cocos.api.notification.service.NotificationService;
import com.cocos.cocos.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PostLikeNotificationListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PostLikedEvent event) {
        notificationService.createForPostLike(
                event.postId(),
                event.memberId(),
                event.likeCount()
        );
    }
}
