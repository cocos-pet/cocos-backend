package com.cocos.cocos.api.symptom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SymptomsOfBodiesResponse(
        @Schema(description = "신체 부위 당 증상 리스트")
        List<SymptomsOfBodyResponse> bodies
) {
    public static SymptomsOfBodiesResponse of(final List<SymptomsOfBodyResponse> bodies) {
        return new SymptomsOfBodiesResponse(bodies);
    }
}
