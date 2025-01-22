package com.cocos.cocos.api.member.service;

import com.cocos.cocos.api.member.dto.response.LoginResponse;
import com.cocos.cocos.api.member.dto.response.MemberProfileResponse;
import com.cocos.cocos.api.member.dto.response.ReissueTokenResponse;
import com.cocos.cocos.api.member.dto.response.TokenResponse;
import com.cocos.cocos.auth.JwtProvider;
import com.cocos.cocos.api.member.dto.response.NicknameExistenceResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.entity.MemberToken;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.member.repository.MemberTokenRepository;
import com.cocos.cocos.enums.member.Platform;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.AppDataS3Client;
import com.cocos.cocos.external.KakaoLoginClient;
import com.cocos.cocos.external.MemberDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberDataS3Client memberDataS3Client;
    private final AppDataS3Client appDataS3Client;
    private final KakaoLoginClient kakaoLoginClient;
    private final JwtProvider jwtProvider;
    private final MemberTokenRepository memberTokenRepository;

    private static final String MEMBER_BASE_IMAGE_URL = "member/baseProfileImage.png";

    @Transactional(readOnly = true)
    public MemberProfileResponse getMemberProfile(final String nickname, final Long memberId) {
        final Long selectedMemberId = (nickname != null) ? findMemberByNickname(nickname) : memberId;
        final Member member = memberRepository.findById(selectedMemberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );
        return MemberProfileResponse.of(member.getNickname(), appDataS3Client.getPresignedUrl(member.getImage()));
    }

    @Transactional
    public LoginResponse login(final String code) {
        final String sub = kakaoLoginClient.login(code);
        Member member = null;
        boolean isCompletedSignUp;
        if (memberRepository.existsBySub(sub)) {
            member = memberRepository.findBySub(sub);

            if (member.getNickname() == null) {
                isCompletedSignUp = false;
            } else {
                isCompletedSignUp = true;
            }
        } else {
            member = memberRepository.save(Member.builder()
                    .email("")
                    .image(MEMBER_BASE_IMAGE_URL)
                    .isAdmin(false)
                    .platform(Platform.KAKAO)
                    .sub(sub)
                    .build()
            );
            isCompletedSignUp = false;
        }
        final String accessToken = jwtProvider.generateAccessToken(member.getId());
        final String refreshToken = jwtProvider.generateRefreshToken(member.getId());
        if (memberTokenRepository.existsByMemberId(member.getId())) {
            final MemberToken memberToken = memberTokenRepository.findByMemberId(member.getId());
            memberToken.updateRefreshToken(refreshToken);
        } else {
            memberTokenRepository.save(MemberToken.builder().memberId(member.getId()).refreshToken(refreshToken).kakaoRefreshToken("").build());
        }
        return LoginResponse.of(TokenResponse.of(accessToken, refreshToken), isCompletedSignUp);
    }

    @Transactional
    public Void logout(final Long memberId) {
        if (memberTokenRepository.existsByMemberId(memberId)) {
            memberTokenRepository.deleteByMemberId(memberId);
        }
        return null;
    }

    @Transactional
    public ReissueTokenResponse reIssueToken(final Long memberId) {
        if (memberTokenRepository.existsByMemberId(memberId)) {
            final MemberToken memberToken = memberTokenRepository.findByMemberId(memberId);
            final String accessToken = jwtProvider.generateAccessToken(memberToken.getMemberId());
            final String refreshToken = jwtProvider.generateRefreshToken(memberToken.getMemberId());
            memberToken.updateRefreshToken(refreshToken);
            return ReissueTokenResponse.of(TokenResponse.of(accessToken, refreshToken));
        }
        return null;
    }

    private Long findMemberByNickname(String nickname) {
        if (nickname != null) {
            final Member member = memberRepository.findByNickname(nickname);
            if (member == null) {
                throw new CocosException((FailMessage.NOT_FOUND_MEMBER));
            }
            return member.getId();
        } else {
            return null;
        }
    }

    ;

    @Transactional
    public NicknameExistenceResponse updateMemberProfile(final String nickname, final Long memberId) {
        if (memberRepository.existsByNickname(nickname)) {
            return NicknameExistenceResponse.of(true);
        } else {
            final Member member = memberRepository.findById(memberId).orElseThrow(
                    () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
            );
            member.updateFields(nickname);
            return NicknameExistenceResponse.of(false);

        }
    }

    public NicknameExistenceResponse checkNickname(final String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            return NicknameExistenceResponse.of(true);
        } else {
            return NicknameExistenceResponse.of(false);

        }
    }
}

