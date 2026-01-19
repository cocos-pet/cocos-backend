package com.cocos.cocos.api.notification.controller;

import com.cocos.cocos.api.notification.dto.response.UnreadNotificationResponse;
import com.cocos.cocos.api.notification.service.NotificationService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerSwagger{
    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<BaseResponse<UnreadNotificationResponse>> hasUnread() {
        return SuccessResponse.success(
                SuccessMessage.OK,
                notificationService.hasUnreadNotification(
                        PrincipalHandler.getMemberIdFromPrincipal()
                )
        );
    }
}
