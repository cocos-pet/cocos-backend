package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.request.LoginRequest;
import com.cocos.cocos.api.member.dto.response.LoginResponse;
import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.ReissueTokenResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member Controller", description = "사용자 관련 API")
public interface MemberControllerSwagger {

    @Operation(summary = "사용자 조회 API", description = "사용자를 조회하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "사용자 조회에 성공했습니다.")
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMemberProfile();

    @Operation(summary = "로그인 API", description = "로그인 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "로그인에 성공했습니다.")
    public ResponseEntity<BaseResponse<LoginResponse>> login(final LoginRequest loginRequest);

    @Operation(summary = "로그아웃 API", description = "로그아웃 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "로그아웃에 성공했습니다.")
    public ResponseEntity<BaseResponse<Void>> logout();

    @Operation(summary = "토큰 재발급 API", description = "토큰 재발급 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "토큰 재발급에 성공했습니다.")
    public ResponseEntity<BaseResponse<ReissueTokenResponse>> reIssueToken();


}
