package com.cocos.cocos.api.symptom.controller;

import com.cocos.cocos.api.symptom.dto.response.SymptomsOfBodiesResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Symptom Controller", description = "증상 관련 API")
public interface SymptomControllerSwagger {

    @Operation(summary = "증상 리스트 조회 API", description = "증상 리스트를 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "증상 리스트 조회 성공")
    @Parameter(name = "bodyIds", description = "신체 부위 아이디 리스트", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "List<Long>"))
    public ResponseEntity<BaseResponse<SymptomsOfBodiesResponse>> getSymptoms(final List<Long> bodyIds);
}
