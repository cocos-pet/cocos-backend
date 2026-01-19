package com.cocos.cocos.api.notification.controller;

import com.cocos.cocos.api.notification.dto.response.UnreadNotificationResponse;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.validation.notification.NotificationIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification Controller", description = "알림 관련 API")
public interface NotificationControllerSwagger {

    @Operation(summary = "읽지 않은 알림 존재 여부 조회 API", description = "읽지 않은 알림이 존재하는지 조회하는 API 입니다.")
    @ApiResponse(
            responseCode = "201",
            description = "읽지 않은 알림 존재 여부 조회에 성공했습니다. "
    )
    public ResponseEntity<BaseResponse<UnreadNotificationResponse>> hasUnread();

    @Operation(summary = "알림 읽음 처리 API", description = "알림을 읽음 처리합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "알림 읽음 처리에 성공했습니다."
    )
    @Parameter(name = "notificationId", description = "알림 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<Void>> read(
            @NotificationIdConstraint final Long notificationId
    );
}
