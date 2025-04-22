package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.request.LoginRequest;
import com.cocos.cocos.api.member.dto.request.ProfileUpdateRequest;
import com.cocos.cocos.api.member.dto.response.LoginResponse;
import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.ReissueTokenResponse;
import com.cocos.cocos.api.member.dto.response.NicknameExistenceResponse;
import com.cocos.cocos.api.member.dto.response.MemberLocationResponse;
import com.cocos.cocos.api.member.service.MemberService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerSwagger {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMemberProfile(
            @RequestParam(name = "nickname", required = false) final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK, memberService.getMemberProfile(nickname, PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @PatchMapping
    public ResponseEntity<BaseResponse<NicknameExistenceResponse>> updateMemberProfile(
            @RequestBody final ProfileUpdateRequest profileUpdateRequest
    ) {
        final NicknameExistenceResponse nicknameExistenceResponse = memberService.updateMemberProfile(
                PrincipalHandler.getMemberIdFromPrincipal(),
                profileUpdateRequest.nickname(),
                profileUpdateRequest.address(),
                profileUpdateRequest.roadAddress(),
                profileUpdateRequest.locationId(),
                profileUpdateRequest.locationType(),
                profileUpdateRequest.latitude(),
                profileUpdateRequest.longitude()
        );
        return SuccessResponse.success(SuccessMessage.OK, nicknameExistenceResponse);
    }

    @GetMapping("/check")
    public ResponseEntity<BaseResponse<NicknameExistenceResponse>> checkNickname(
            @RequestParam(name = "nickname") final String nickname
    ) {
        final NicknameExistenceResponse nicknameExistenceResponse = memberService.checkNickname(nickname);
        return SuccessResponse.success(SuccessMessage.OK, nicknameExistenceResponse);
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

    @GetMapping("/location")
    public ResponseEntity<BaseResponse<MemberLocationResponse>> getMemberLocation() {
        return SuccessResponse.success(SuccessMessage.OK, memberService.getMemberLocation(PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @PostMapping("/hospitals/{hospitalId}")
    public ResponseEntity<BaseResponse<Void>> addMemberHospital(@PathVariable(name = "hospitalId") final Long hospitalId) {
        memberService.addMemberHospital(hospitalId, PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.OK, null);
    }
}
