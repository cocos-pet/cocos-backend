package com.cocos.cocos.api.disease.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DiseasesOfBodiesResponse(
        @Schema(description = "신체 부위 당 질병 리스트")
        List<DiseasesOfBodyResponse> bodies
) {
    public static DiseasesOfBodiesResponse of(final List<DiseasesOfBodyResponse> bodies) {
        return new DiseasesOfBodiesResponse(bodies);
    }
}
