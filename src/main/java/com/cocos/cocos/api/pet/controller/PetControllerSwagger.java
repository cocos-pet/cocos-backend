package com.cocos.cocos.api.pet.controller;

import com.cocos.cocos.api.pet.dto.response.PetOwnerCheckResponse;
import com.cocos.cocos.api.pet.dto.response.PetResponse;
import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import com.cocos.cocos.validation.pet.PetIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Pet Controller", description = "반려동물 관련 API")
public interface PetControllerSwagger {

    @Operation(summary = "사용자 반려동물 추가 API", description = "사용자 반려동물 추가 API입니다. ")
    @ApiResponse(
            responseCode = "201",
            description = "요청이 성공했습니다. ")
    public ResponseEntity<BaseResponse<Void>> addPet(
            @RequestBody @Valid final PetCreateRequest petCreateRequest
    );

    @Operation(summary = "사용자 반려동물 수정 API", description = "사용자 반려동물 수정 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다. ")
    public ResponseEntity<BaseResponse<Void>> updatePet(
            @Parameter(name = "petId", description = "애완동물 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long")) @PetIdConstraint final Long petId,
            @RequestBody @Valid final PetUpdateRequest petUpdateRequest
    );

    @Operation(summary = "사용자 반려동물 조회 API", description = "사용자 반려동물 조회 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다.")
    public ResponseEntity<BaseResponse<PetResponse>> getPet(
            @RequestParam @MemberNicknameConstraint final String nickname
    );

    @Operation(summary = "반려동물 등록 API", description = "사용자가 반려동물을 등록했는지 확인하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다.")
    @GetMapping("/owner/check")
    public ResponseEntity<BaseResponse<PetOwnerCheckResponse>> checkOwner();
}
