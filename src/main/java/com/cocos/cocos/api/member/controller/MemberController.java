package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.request.LoginRequest;
import com.cocos.cocos.api.member.dto.response.LoginResponse;
import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.ReissueTokenResponse;
import com.cocos.cocos.api.member.service.MemberService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwagger{
    private static final Long memberId = 1L;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMemberProfile() {
        return SuccessResponse.success(SuccessMessage.OK, memberService.getMemberProfile(memberId));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @RequestBody final LoginRequest loginRequest
    ) {
        return SuccessResponse.success(SuccessMessage.OK, memberService.login(loginRequest.code()));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout() {
        return SuccessResponse.success(SuccessMessage.OK, memberService.logout(PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<BaseResponse<ReissueTokenResponse>> reIssueToken() {
        return SuccessResponse.success(SuccessMessage.OK, memberService.reIssueToken(PrincipalHandler.getMemberIdFromPrincipal()));
    }
}
