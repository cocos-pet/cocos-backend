package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.request.LoginRequest;
import com.cocos.cocos.api.member.dto.response.LoginResponse;
import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.ReissueTokenResponse;
import com.cocos.cocos.api.member.dto.response.NicknameExistenceResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Member Controller", description = "사용자 관련 API")
public interface MemberControllerSwagger {

    @Operation(summary = "사용자 조회 API", description = "사용자를 조회하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "사용자 조회에 성공했습니다.")
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMemberProfile(
            @RequestParam(name = "nickname", required = false) final String nickname
    );

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

    @Operation(summary = "사용자 정보 업데이트 API", description = "사용자를 정보 업데이트 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다. ")
    public ResponseEntity<BaseResponse<NicknameExistenceResponse>> updateMemberProfile(@RequestParam final String nickname);

    @Operation(summary = "닉네임 중복 조회 API", description = "중복된 닉네임이 있는지 검사합니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다. ")
    public ResponseEntity<BaseResponse<NicknameExistenceResponse>> checkNickname(@RequestParam final String nickname);
}
