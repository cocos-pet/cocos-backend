package com.cocos.cocos.api.body.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BodiesResponse(
        @Schema(description = "신체 부위 리스트")
        List<BodyResponse> bodies
) {
    public static BodiesResponse of(final List<BodyResponse> bodies) {
        return new BodiesResponse(bodies);
    }
}
