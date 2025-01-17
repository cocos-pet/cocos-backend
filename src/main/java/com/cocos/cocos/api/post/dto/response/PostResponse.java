package com.cocos.cocos.api.post.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponse(
        Long id,
        String breed,
        int petAge,
        String title,
        String content,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String image,
        String category
) {
}
