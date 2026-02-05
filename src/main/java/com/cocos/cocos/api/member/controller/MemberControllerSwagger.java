package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.request.LoginRequest;
import com.cocos.cocos.api.member.dto.request.ProfileUpdateRequest;

import com.cocos.cocos.api.member.dto.response.LoginResponse;
import com.cocos.cocos.api.member.dto.response.MemberHospitalResponse;
import com.cocos.cocos.api.member.dto.response.MemberLocationResponse;
import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.MemberRecentReviewResponse;
import com.cocos.cocos.api.member.dto.response.MemberReviewTermsAgreeResponse;
import com.cocos.cocos.api.member.dto.response.NicknameExistenceResponse;
import com.cocos.cocos.api.member.dto.response.ReissueTokenResponse;

import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Member Controller", description = "사용자 관련 API")
public interface MemberControllerSwagger {

    @Operation(summary = "사용자 조회 API", description = "사용자를 조회하는 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "사용자 조회에 성공했습니다.")
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMemberProfile(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname
    );

    @Operation(summary = "로그인 API", description = "로그인 API 입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "로그인에 성공했습니다.")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid final LoginRequest loginRequest);

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
    public ResponseEntity<BaseResponse<NicknameExistenceResponse>> updateMemberProfile(@RequestBody final ProfileUpdateRequest profileUpdateRequest);

    @Operation(summary = "닉네임 중복 조회 API", description = "중복된 닉네임이 있는지 검사합니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다. ")
    public ResponseEntity<BaseResponse<NicknameExistenceResponse>> checkNickname(@RequestParam final String nickname);

    @Operation(summary = "사용자 위치 조회 API", description = "사용자위치에 등록된 동 정보를 반환합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다. "
    )
    public ResponseEntity<BaseResponse<MemberLocationResponse>> getMemberLocation();

    @Operation(summary = "사용자 즐겨찾는 병원 조회 API", description = "사용자의 즐겨찾는 병원을 조회합니다. 없으면 보내지 않습니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    public ResponseEntity<BaseResponse<MemberHospitalResponse>> getMemberHospital(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname
    );

    @Operation(summary = "사용자 즐겨찾는 병원 추가&수정 API", description = "사용자의 즐겨찾는 병원을 수정합니다. 이전에 등록된 병원이 없는 경우 추가합니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    public ResponseEntity<BaseResponse<Void>> updateMemberHospital(
            @PathVariable(name = "hospitalId") @HospitalIdConstraint final Long hospitalId
    );

    @Operation(summary = "리뷰 약관 동의 업데이트 API", description = "리뷰 약관 동의 여부를 업데이트하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    public ResponseEntity<BaseResponse<Void>> updateMemberReviewTerms();

    @Operation(summary = "리뷰 약관 동의 여부 조회 API", description = "리뷰 약관 동의 여부를 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    public ResponseEntity<BaseResponse<MemberReviewTermsAgreeResponse>> getMemberReviewTerms();

    @Operation(summary = "사용자 진단 정보 있는 최근 리뷰 조회 API", description = "사용자의 진단 정보 있는 최근 리뷰 조회 API입니다. ")
    @ApiResponse(
            responseCode = "200",
            description = "요청에 성공했습니다."
    )
    public ResponseEntity<BaseResponse<MemberRecentReviewResponse>> getRecentMemberReview(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname
    );
}
