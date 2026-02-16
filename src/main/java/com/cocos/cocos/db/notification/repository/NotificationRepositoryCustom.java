package com.cocos.cocos.db.notification.repository;

import com.cocos.cocos.db.notification.entity.Notification;
import com.cocos.cocos.enums.notification.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepositoryCustom {
    List<Notification> findNotifications(
            Long memberId,
            List<NotificationType> types,
            LocalDateTime cursorCreatedAt,
            Long cursorId,
            int size
    );
}
