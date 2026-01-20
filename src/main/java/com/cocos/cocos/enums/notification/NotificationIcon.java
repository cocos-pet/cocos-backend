package com.cocos.cocos.enums.notification;

public enum NotificationIcon {

    COMMENT("notification/message.png"),
    SUB_COMMENT("notification/message.png"),
    POST_LIKE_MILESTONE("notification/cheers.png"),
    MAGAZINE_PUBLISHED("post_category/magazine.png");

    private final String imageKey;

    NotificationIcon(String imageKey) {
        this.imageKey = imageKey;
    }

    public static String imageKeyOf(NotificationType type) {
        return NotificationIcon.valueOf(type.name()).imageKey;
    }
}
