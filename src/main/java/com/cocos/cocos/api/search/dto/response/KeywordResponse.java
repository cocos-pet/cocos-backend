package com.cocos.cocos.api.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record KeywordResponse(
        @Schema(description = "검색 기록 아이디", example = "1")
        Long id,
        @Schema(description = "검색 내용", example = "포메")
        String content
) {
    public static KeywordResponse of(final Long id, final String content) {
        return new KeywordResponse(id, content);
    }
}
