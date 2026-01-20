package com.cocos.cocos.api.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationListResponse(

        @Schema(description = "리스트의 마지막 알림 생성 시간")
        LocalDateTime cursorCreatedAt,

        @Schema(description = "리스트의 마지막 알림 ID")
        Long cursorId,

        @Schema(description = "알림 리스트")
        List<NotificationResponse> notifications

) {
    public static NotificationListResponse of(
            LocalDateTime cursorCreatedAt,
            Long cursorId,
            List<NotificationResponse> notifications
    ) {
        return new NotificationListResponse(cursorCreatedAt, cursorId, notifications);
    }
}
