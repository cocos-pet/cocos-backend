package com.cocos.cocos.test.controller;

import com.cocos.cocos.api.member.dto.response.TokenResponse;
import com.cocos.cocos.test.dto.FakeLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Tag(name = "Test Auth Controller", description = "테스트 인증 관련 API")
public interface TestAuthControllerSwagger {

    @Operation(summary = "Test Login API", description = "테스트용 로그인 API입니다. (Local/Dev/Test only)")
    @ApiResponse(
            responseCode = "200",
            description = "토큰 발급 성공",
            content = @Content(schema = @Schema(type = "array", implementation = TokenResponse.class)))
    ResponseEntity<List<TokenResponse>> fakeLogin(
            @Parameter(in = ParameterIn.HEADER, name = "X-Test-Auth-Secret", description = "테스트 인증 시크릿 키", required = true)
            @RequestHeader("X-Test-Auth-Secret") final String secretKey,
            @RequestBody final FakeLoginRequest memberIds);
}
