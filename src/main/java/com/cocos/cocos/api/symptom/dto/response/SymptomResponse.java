package com.cocos.cocos.api.symptom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SymptomResponse(
        @Schema(description = "증상 아이디", example = "1")
        Long id,
        @Schema(description = "증상 이름", example = "머리가 아픔")
        String name
) {
    public static SymptomResponse of(final Long id, final String name) {
        return new SymptomResponse(id, name);
    }
}
