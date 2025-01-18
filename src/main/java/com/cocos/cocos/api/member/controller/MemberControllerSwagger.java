package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
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
}
