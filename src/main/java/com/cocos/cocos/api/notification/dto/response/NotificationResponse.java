package com.cocos.cocos.api.notification.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 응답 DTO")
public record NotificationResponse(

        @Schema(description = "알림 고유 ID", example = "123")
        Long id,

        @Schema(description = "알림 타입", example = "COMMENT", allowableValues = {"COMMENT", "SUB_COMMENT", "POST_LIKE_MILESTONE", "MAGAZINE_PUBLISHED"})
        String type,

        @Schema(description = "알림 읽음 여부", example = "false")
        boolean isRead,

        @Schema(description = "알림 생성 시각", example = "2026-01-20T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "알림과 연관된 게시글 ID", example = "45")
        Long postId,

        @Schema(description = "알림 대상 ID (댓글 ID, 대댓글 ID, 게시글 ID 등 알림 타입에 따라 다름)", example = "78")
        Long targetId,

        @Schema(description = "알림 제목 (게시글 제목)", example = "강아지 헥헥거림 증상")
        String title,

        @Schema(description = "알림 내용 (댓글 내용 요약, 매거진 요약 등)", example = "닉네임님의 댓글: 저희 집 강아지도 비슷한 증상이 있었어요.")
        String content,

        @Schema(description = "알림을 발생시킨 사용자 닉네임 (댓글/대댓글 알림에서 사용)", example = "코코")
        String actorNickname,

        @Schema(description = "좋아요 마일스톤 수치 (POST_LIKE_MILESTONE 타입에서만 사용, 그 외 null)", example = "10", nullable = true)
        Integer milestone,

        @Schema(description = "알림 아이콘", nullable = true)
        String iconImageUrl
) {}
