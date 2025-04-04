package com.cocos.cocos.api.report.controller;

import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.enums.report.ReportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Report Controller", description = "신고 관련 API")
public interface ReportControllerSwagger {

    @Operation(summary = "신고 API", description = "신고하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청이 성공했습니다. ")
    @Parameter(name = "targetId", description = "신고 대상 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    @Parameter(name = "reportType", description = "신고 대상 타입(POST | REVIEW | COMMENT | SUB_COMMENT)", in = ParameterIn.QUERY, required = true, schema = @Schema(type = "Enum"))
    public ResponseEntity<BaseResponse<Void>> addReport(
            @PathVariable(name = "targetId") final Long targetId,
            @RequestParam(name = "reportType") final ReportType reportType
    );
}
