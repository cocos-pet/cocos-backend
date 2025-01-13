package com.cocos.cocos.api.symptom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SymptomsOfBodyResponse(
        @Schema(description = "신체 부위 아이디", example = "1")
        Long id,
        @Schema(description = "신체 부위 이름", example = "머리")
        String name,
        @Schema(description = "증상 리스트")
        List<SymptomResponse> symptoms
) {
    public static SymptomsOfBodyResponse of(final Long id, final String name, final List<SymptomResponse> symptoms) {
        return new SymptomsOfBodyResponse(id, name, symptoms);
    }
}
