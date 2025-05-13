package com.cocos.cocos.api.pet.controller;

import com.cocos.cocos.api.pet.dto.response.PetResponse;
import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Pet Controller", description = "애완동물 관련 API")
public interface PetControllerSwagger {

    @Operation(summary = "사용자 애완동물 추가 API", description = "사용자 애완동물 추가 API입니다. ")
    @ApiResponse(
            responseCode = "201",
            description = "요청이 성공했습니다. ")
    public ResponseEntity<BaseResponse<Void>> addPet(
            @RequestBody final PetCreateRequest petCreateRequest
    );

    @Operation(summary = "사용자 애완동물 수정 API", description = "사용자 애완동물 수정 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다. ")
    public ResponseEntity<BaseResponse<Void>> updatePet(
            @Parameter(name = "petId", description = "애완동물 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long")) final Long petId,
            @RequestBody final PetUpdateRequest petUpdateRequest
    );

    @Operation(summary = "사용자 애완동물 조회 API", description = "사용자 애완동물 조회 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다.")
    public ResponseEntity<BaseResponse<PetResponse>> getPet(
            @RequestParam final String nickname
    );
}
