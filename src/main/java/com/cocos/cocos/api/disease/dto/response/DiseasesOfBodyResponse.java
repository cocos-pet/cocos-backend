package com.cocos.cocos.api.disease.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DiseasesOfBodyResponse(
        @Schema(description = "신체 부위 아이디", example = "1")
        Long id,
        @Schema(description = "신체 부위 이름", example = "심장")
        String name,
        @Schema(description = "질병 리스트")
        List<DiseaseResponse> diseases
) {
    public static DiseasesOfBodyResponse of(final Long id, final String name, final List<DiseaseResponse> diseases) {
        return new DiseasesOfBodyResponse(id, name, diseases);
    }
}
