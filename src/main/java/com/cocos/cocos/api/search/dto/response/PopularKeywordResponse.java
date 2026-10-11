package com.cocos.cocos.api.search.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PopularKeywordResponse(
        @Schema(description = "인기 검색어 (배열 순서 = 순위)", example = "[\"강아지 설사\", \"좋은 병원 찾기\"]")
        List<String> keywords
) {
    public static PopularKeywordResponse of(final List<String> keywords) {
        return new PopularKeywordResponse(keywords);
    }
}
