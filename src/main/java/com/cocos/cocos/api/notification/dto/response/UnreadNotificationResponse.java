package com.cocos.cocos.api.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UnreadNotificationResponse(
        @Schema(description = "읽지 않은 알림이 있는지 여부", example = "true")
        boolean hasUnread
) {
}
