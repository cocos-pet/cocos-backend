package com.cocos.cocos.api.pet.controller;

import com.cocos.cocos.api.pet.dto.request.PetGenerationRequest;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Pet Controller", description = "애완동물 관련 API")
public interface PetControllerSwagger {

    @Operation(summary = "사용자 애완동물 추가 API", description = "사용자 애완동물 추가 API입니다. ")
    @ApiResponse(
            responseCode = "201",
            description = "애완동물 추가 성공")
    public ResponseEntity<BaseResponse<Void>> addPet(
            @RequestBody final PetGenerationRequest petGenerationRequest
    );
}