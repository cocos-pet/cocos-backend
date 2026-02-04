package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.request.LoginRequest;
import com.cocos.cocos.api.member.dto.request.ProfileUpdateRequest;
import com.cocos.cocos.api.member.dto.response.*;
import com.cocos.cocos.api.member.service.MemberService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.util.validation.EntityExistsValidator;
import com.cocos.cocos.validation.hospital.HospitalIdConstraint;
import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import jakarta.validation.Valid;
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
    private final EntityExistsValidator entityExistsValidator;

    @GetMapping
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMemberProfile(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname
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
            @Valid @RequestBody final LoginRequest loginRequest
    ) {
        return SuccessResponse.success(SuccessMessage.OK, memberService.login(loginRequest.code(), loginRequest.redirectUri()));
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

    @GetMapping("/hospitals")
    public ResponseEntity<BaseResponse<MemberHospitalResponse>> getMemberHospital(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK, memberService.getMemberHospital(nickname, PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @PatchMapping("/hospitals/{hospitalId}")
    public ResponseEntity<BaseResponse<Void>> updateMemberHospital(@PathVariable(name = "hospitalId") @HospitalIdConstraint final Long hospitalId) {
        entityExistsValidator.validatePetByMemberId(PrincipalHandler.getMemberIdFromPrincipal());
        memberService.updateMemberHospital(hospitalId, PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @PatchMapping("/reviews/agree")
    public ResponseEntity<BaseResponse<Void>> updateMemberReviewTerms() {
        memberService.updateReviewTermsAgree(PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @GetMapping("/reviews/agree")
    public ResponseEntity<BaseResponse<MemberReviewTermsAgreeResponse>> getMemberReviewTerms() {
        return SuccessResponse.success(SuccessMessage.OK, memberService.getMemberReviewTermsAgree(PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<BaseResponse<Void>> deactivateMember() {
        memberService.deactivateMember(PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @GetMapping("/reviews/recent")
    public ResponseEntity<BaseResponse<MemberRecentReviewResponse>> getRecentMemberReview(
            @RequestParam(name = "nickname", required = false)  @MemberNicknameConstraint final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK, memberService.getRecentReview(nickname, PrincipalHandler.getMemberIdFromPrincipal()));
    }
}
