package com.cocos.cocos.api.hospital.controller;

import com.cocos.cocos.api.hospital.dto.request.HospitalListRequest;
import com.cocos.cocos.api.hospital.dto.response.HospitalDetailResponse;
import com.cocos.cocos.api.hospital.dto.response.HospitalListResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Hospital Controller", description = "병원 관련 API")
public interface HospitalControllerSwagger {

    @Operation(summary = "병원 리스트 조회 API", description = "병원 검색, 메인 페이지 등에서 정렬 기준에 따라 병원 리스트를 조회할 때 사용하는 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    public ResponseEntity<BaseResponse<HospitalListResponse>> getHospitals(
            @Valid @RequestBody HospitalListRequest hospitalListRequest
    );

    @Operation(summary = "병원 상세 조회 API", description = "병원 상세 정보 조회 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    @Parameter(name = "hospitalId", description = "병원 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<HospitalDetailResponse>> getHospitalDetail(
            @PathVariable final Long hospitalId
    );
}
