package com.cocos.cocos.api.notification.listener;

import com.cocos.cocos.api.notification.service.NotificationService;
import com.cocos.cocos.event.PostSubCommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PostSubCommentNotificationListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PostSubCommentEvent event) {
        notificationService.createForSubComment(event);
    }
}
