package com.cocos.cocos.api.notification.controller;

import com.cocos.cocos.api.notification.dto.response.NotificationListResponse;
import com.cocos.cocos.api.notification.dto.response.UnreadNotificationResponse;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.enums.notification.NotificationCategory;
import com.cocos.cocos.validation.notification.NotificationIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Tag(name = "Notification Controller", description = "알림 관련 API")
public interface NotificationControllerSwagger {

    @Operation(summary = "읽지 않은 알림 존재 여부 조회 API", description = "읽지 않은 알림이 존재하는지 조회하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
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

    @Operation(
            summary = "알림 리스트 조회 API",
            description = "카테고리별(MAGAZINE, MY) 알림 리스트를 cursor 기반 페이지네이션으로 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "알림 리스트 조회에 성공했습니다."
    )
    @Parameter(
            name = "category",
            description = "알림 카테고리 (MAGAZINE | MY)",
            in = ParameterIn.QUERY,
            required = true,
            schema = @Schema(type = "string", allowableValues = {"MAGAZINE", "MY"})
    )
    @Parameter(
            name = "cursorCreatedAt",
            description = "마지막 알림의 생성 시간 (cursor 기반 페이지네이션)",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "string", format = "date-time")
    )
    @Parameter(
            name = "cursorId",
            description = "마지막 알림의 아이디 (cursor 기반 페이지네이션)",
            in = ParameterIn.QUERY,
            schema = @Schema(type = "Long")
    )
    public ResponseEntity<BaseResponse<NotificationListResponse>> getNotifications(
            @RequestParam(name = "category") final NotificationCategory category,

            @RequestParam(name = "cursorCreatedAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            final LocalDateTime cursorCreatedAt,

            @RequestParam(name = "cursorId", required = false)
            @NotificationIdConstraint
            final Long cursorId
    );
}
