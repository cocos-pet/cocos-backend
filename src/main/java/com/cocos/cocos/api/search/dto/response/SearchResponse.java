package com.cocos.cocos.api.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SearchResponse(
        @Schema(description = "검색 내역")
        List<KeywordResponse> keywords
) {
    public static SearchResponse of(final List<KeywordResponse> keywords) {
        return new SearchResponse(keywords);
    }
}
