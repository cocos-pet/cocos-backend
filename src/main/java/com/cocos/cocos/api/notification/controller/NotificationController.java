package com.cocos.cocos.api.notification.controller;

import com.cocos.cocos.api.notification.dto.response.NotificationListResponse;
import com.cocos.cocos.api.notification.dto.response.UnreadNotificationResponse;
import com.cocos.cocos.api.notification.service.NotificationService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.enums.notification.NotificationCategory;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.validation.notification.NotificationIdConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerSwagger {
    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<BaseResponse<UnreadNotificationResponse>> hasUnread() {
        return SuccessResponse.success(SuccessMessage.CREATED, notificationService.hasUnreadNotification(PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<BaseResponse<Void>> read(@PathVariable(name = "notificationId") @NotificationIdConstraint final Long notificationId) {
        notificationService.readNotification(PrincipalHandler.getMemberIdFromPrincipal(), notificationId);
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<NotificationListResponse>> getNotifications(
            @RequestParam(name = "category") final NotificationCategory category,
            @RequestParam(name = "cursorCreatedAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime cursorCreatedAt,
            @RequestParam(name = "cursorId", required = false)
            @NotificationIdConstraint final Long cursorId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, notificationService.getNotifications(
                PrincipalHandler.getMemberIdFromPrincipal(),
                category,
                cursorCreatedAt,
                cursorId
        ));
    }

}
