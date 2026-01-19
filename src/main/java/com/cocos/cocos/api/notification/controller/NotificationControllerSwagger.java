package com.cocos.cocos.api.notification.controller;

import com.cocos.cocos.api.notification.dto.response.UnreadNotificationResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification Controller", description = "알림 관련 API")
public interface NotificationControllerSwagger {

    @Operation(summary = "읽지 않은 알림 존재 여부 조회 API", description = "읽지 않은 알림이 존재하는지 조회하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "읽지 않은 알림 존재 여부 조회에 성공했습니다. "
    )
    public ResponseEntity<BaseResponse<UnreadNotificationResponse>> hasUnread();
}
