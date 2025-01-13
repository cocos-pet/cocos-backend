package com.cocos.cocos.api.body.controller;

import com.cocos.cocos.api.body.dto.response.BodiesResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Body Controller", description = "신체 부위 관련 API")
public interface BodyControllerSwagger {

    @Operation(summary = "신체 부위 조회 API", description = "신체 부위를 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "신체 부위 조회 성공")
    public ResponseEntity<BaseResponse<BodiesResponse>> getBodies();
}
