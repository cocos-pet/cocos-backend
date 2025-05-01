package com.cocos.cocos.api.member.service;

import com.cocos.cocos.api.member.dto.response.*;
import com.cocos.cocos.auth.JwtProvider;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.city.entity.City;
import com.cocos.cocos.db.city.repository.CityRepository;
import com.cocos.cocos.db.district.entity.District;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.db.member.entity.Member;
import com.cocos.cocos.db.member.entity.MemberAddress;
import com.cocos.cocos.db.member.entity.MemberToken;
import com.cocos.cocos.db.member.repository.MemberAddressRepository;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.member.repository.MemberTokenRepository;
import com.cocos.cocos.enums.location.LocationType;
import com.cocos.cocos.enums.member.Platform;
import com.cocos.cocos.enums.message.FailMessage;
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
    private final KakaoLoginClient kakaoLoginClient;
    private final JwtProvider jwtProvider;
    private final MemberTokenRepository memberTokenRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final HospitalRepository hospitalRepository;

    @Transactional(readOnly = true)
    public MemberProfileResponse getMemberProfile(final String nickname, final Long memberId) {
        final Member member = findMember(nickname, memberId);
        return MemberProfileResponse.of(member.getNickname(), memberDataS3Client.getPresignedUrl(member.getImage()));
    }

    @Transactional
    public LoginResponse login(final String code) {
        //ToDo: 코드 역할에 따라 간격 띄우는 것 필요
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
            member = memberRepository.save(Member.createDefaultMember(sub, Platform.KAKAO));
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
        if (!memberAddressRepository.existsByMemberId(member.getId())) {
            memberAddressRepository.save(
                    MemberAddress.createDefaultMemberAddress(member.getId())
            );
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

    @Transactional
    public NicknameExistenceResponse updateMemberProfile(final Long memberId, final String nickname,
                                                         final String address, final String roadAddress,
                                                         final Long locationId, final LocationType locationType,
                                                         final Double latitude, final Double longitude) {

        final Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );

        if (address != null && roadAddress != null) {
            updateMemberLocation(memberId, address, roadAddress, locationId, locationType, latitude, longitude);

        }

        if (memberRepository.existsByNickname(nickname)) {
            return NicknameExistenceResponse.of(true);
        } else {
            member.updateNickname(nickname);
            return NicknameExistenceResponse.of(false);

        }
    }

    @Transactional(readOnly = true)
    public NicknameExistenceResponse checkNickname(final String nickname) {
        return NicknameExistenceResponse.of(memberRepository.existsByNickname(nickname));
    }

    @Transactional(readOnly = true)
    public MemberLocationResponse getMemberLocation(final Long memberId) {
        final MemberAddress memberAddress = memberAddressRepository.findByMemberId(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER_ADDRESS)
        );
        if (memberAddress.getLocationType().equals(LocationType.CITY)) {
            final City city = cityRepository.findById(memberAddress.getLocationId()).orElseThrow(
                    () -> new CocosException(FailMessage.NOT_FOUND_CITY)
            );
            return MemberLocationResponse.of(city.getId(), city.getName(), LocationType.CITY.toString());
        } else if (memberAddress.getLocationType().equals(LocationType.DISTRICT)) {
            final District district = districtRepository.findById(memberAddress.getLocationId()).orElseThrow(
                    () -> new CocosException(FailMessage.NOT_FOUND_DISTRICT)
            );
            return MemberLocationResponse.of(district.getId(), district.getName(), LocationType.DISTRICT.toString());
        }
        throw new CocosException(FailMessage.BAD_REQUEST);
    }

    @Transactional(readOnly = true)
    public MemberHospitalResponse getMemberHospital(final String nickname, final Long memberId) {
        final Member member = findMember(nickname, memberId);
        if (member.getMyHospitalId() == null) {
            return null;
        }

        final Hospital hospital = hospitalRepository.findById(member.getMyHospitalId()).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_HOSPITAL));
        return MemberHospitalResponse.of(hospital.getId(), hospital.getName(), hospital.getDisplayAddress());
    }

    @Transactional
    public void updateMemberHospital(final Long hospitalId, final Long memberId) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new CocosException(FailMessage.NOT_FOUND_HOSPITAL);
        }
        final Member member = memberRepository.findById(memberId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
        member.updateMyHospitalId(hospitalId);
    }

    public Member findMember(final String nickname, final Long memberId) {
        if (nickname != null) {
            return memberRepository.findByNickname(nickname).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
        }
        return memberRepository.findById(memberId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER));
    }

    private void updateMemberLocation(final Long memberId, final String address, final String roadAddress,
                                      final Long locationId, final LocationType locationType,
                                      final Double latitude, final Double longitude) {
        final MemberAddress memberAddress = memberAddressRepository.findByMemberId(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER_ADDRESS)
        );
        memberAddress.updateAddress(address, roadAddress, locationId, latitude, longitude, locationType);
    }

    @Transactional
    public void updateReviewTermsAgree(final Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );

        member.updateReviewTermsAgree();
    }

    @Transactional(readOnly = true)
    public MemberReviewTermsAgreeResponse getMemberReviewTermsAgree(final Long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_MEMBER)
        );

        return MemberReviewTermsAgreeResponse.of(member.isReviewTermsAgree());
    }
}
