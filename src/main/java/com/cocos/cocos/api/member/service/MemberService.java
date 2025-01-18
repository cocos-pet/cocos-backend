package com.cocos.cocos.api.member.service;

import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.NicknameExistenceResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.MemberDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberDataS3Client memberDataS3Client;

    @Transactional(readOnly = true)
    public MemberProfileResponse getMemberProfile(final Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );
        return MemberProfileResponse.of(member.getNickname(), memberDataS3Client.getPresignedUrl(member.getImage()));
    }

    @Transactional
    public NicknameExistenceResponse updateMemberProfile(final String nickname, final Long memberId) {
        if (memberRepository.existsByNickname(nickname)) {
            return NicknameExistenceResponse.of(true);
        } else {
            final Member member = memberRepository.findById(memberId).orElseThrow(
                    ()-> new CocosException(FailMessage.NOT_FOUND_MEMBER)
            );
            member.updateFields(nickname);
            return NicknameExistenceResponse.of(false);
        }
    }
}
