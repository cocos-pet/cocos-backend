package com.cocos.cocos.api.disease.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DiseaseResponse(
        @Schema(description = "질병 아이디", example = "1")
        Long id,
        @Schema(description = "질병 이름", example = "심장병")
        String name
) {
    public static DiseaseResponse of(final Long id, final String name) {
        return new DiseaseResponse(id, name);
    }
}
