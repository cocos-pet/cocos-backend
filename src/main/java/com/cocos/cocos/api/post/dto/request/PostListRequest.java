package com.cocos.cocos.api.post.dto.request;

import com.cocos.cocos.enums.post.SortCriteria;

import java.time.LocalDateTime;
import java.util.List;

public record PostListRequest(
        String keyword,
        List<Long> animalIds,
        List<Long> symptomIds,
        List<Long> diseaseIds,
        SortCriteria sortBy,
        Long cursorId,
        Long categoryId,
        Long likeCount,
        LocalDateTime createAt
) {
}
