package com.cocos.cocos.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "Test Controller", description = "테스트 관련 API")
public interface TestControllerSwagger {

    @Operation(summary = "HealthCheck API", description = "HealthCheck 테스트하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "healthCheck 테스트 성공",
            content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> healthCheck();

    @Operation(summary = "Swagger Token API", description = "Swagger에서의 Token적용을 테스트하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "Token 테스트 성공",
            content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<String> tokenCheck(final HttpServletRequest request);
}
