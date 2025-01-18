package com.cocos.cocos.api.member.controller;

import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.NicknameExistenceResponse;
import com.cocos.cocos.api.member.service.MemberService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/members")
@RequiredArgsConstructor
public class MemberController {
    private static final Long memberId = 1L;
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<BaseResponse<MemberProfileResponse>> getMemberProfile() {
        return SuccessResponse.success(SuccessMessage.OK, memberService.getMemberProfile(memberId));
    }

    @PatchMapping
    public ResponseEntity<BaseResponse<NicknameExistenceResponse>> updateMemberProfile(
            @RequestParam(name = "nickname") final String nickname
    ) {
        final NicknameExistenceResponse nicknameExistenceResponse = memberService.updateMemberProfile(nickname, memberId);
        return SuccessResponse.success(SuccessMessage.OK, nicknameExistenceResponse);
    }
}
