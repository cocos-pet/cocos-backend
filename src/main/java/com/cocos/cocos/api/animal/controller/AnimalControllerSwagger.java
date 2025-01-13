package com.cocos.cocos.api.animal.controller;

import com.cocos.cocos.api.animal.dto.response.AnimalsResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Animal Controller", description = "동물 관련 API")
public interface AnimalControllerSwagger {

    @Operation(summary = "동물 조회 API", description = "동물을 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "동물 조회 성공"
    )
    public ResponseEntity<BaseResponse<AnimalsResponse>> getAnimals();
}
