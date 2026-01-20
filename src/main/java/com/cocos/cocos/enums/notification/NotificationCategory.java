package com.cocos.cocos.enums.notification;

import lombok.Getter;

import java.util.List;

@Getter
public enum NotificationCategory {

    MAGAZINE(
            NotificationType.MAGAZINE_PUBLISHED
    ),
    MY(
            NotificationType.COMMENT,
            NotificationType.SUB_COMMENT,
            NotificationType.POST_LIKE_MILESTONE
    );

    private final List<NotificationType> notificationTypes;

    NotificationCategory(NotificationType... types) {
        this.notificationTypes = List.of(types);
    }

}
