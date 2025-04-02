package com.cocos.cocos.api.location.controller;

import com.cocos.cocos.api.location.dto.response.LocationResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Location Controller", description = "지역 관련 API")
public interface LocationControllerSwagger {

    @Operation(summary = "지역 조회 API", description = "전체 지역을 조회하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다.")
    public ResponseEntity<BaseResponse<LocationResponse>> getLocations();
}
