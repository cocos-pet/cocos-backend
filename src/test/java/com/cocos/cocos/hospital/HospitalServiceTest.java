package com.cocos.cocos.hospital;

import com.cocos.cocos.api.hospital.dto.response.HospitalDetailResponse;
import com.cocos.cocos.api.hospital.service.HospitalService;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.entity.HospitalTag;
import com.cocos.cocos.db.hospital.entity.HospitalTagMapping;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.db.hospital.repository.HospitalTagMappingRepository;
import com.cocos.cocos.db.hospital.repository.HospitalTagRepository;
import com.cocos.cocos.external.AppDataS3Client;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("병원 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class HospitalServiceTest {

    @InjectMocks
    private HospitalService hospitalService;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private HospitalTagRepository hospitalTagRepository;

    @Mock
    private HospitalTagMappingRepository hospitalTagMappingRepository;

    @Mock
    private AppDataS3Client appDataS3Client;

    @Test
    @DisplayName("병원 상세 정보를 조회할 수 있다.")
    void getHospitalDetail() {
        //given
        final Long hospitalId = 1L;
        final Long hospitalTagId1 = 2L;

        final Hospital hospital = Hospital.builder()
                .name("병원 이름")
                .image("병원 이미지")
                .introduction("병원 소개")
                .phoneNumber("병원 전화번호")
                .address("병원 주소")
                .roadAddress("병원 도로명주소")
                .latitude(35.0)
                .longitude(128.0)
                .reviewCount(0)
                .townId(1L)
                .build();

        final HospitalTag hospitalTag1 = HospitalTag.builder()
                .label("라벨1")
                .build();

        final HospitalTag hospitalTag2 = HospitalTag.builder()
                .label("라벨2")
                .build();

        final HospitalTagMapping hospitalTagMapping = HospitalTagMapping.builder()
                .hospitalTagId(hospitalTagId1)
                .hospitalId(hospitalId)
                .build();

        final List<HospitalTagMapping> hospitalTagMappings = new ArrayList<>(List.of(hospitalTagMapping));
        final List<HospitalTag> hospitalTags = new ArrayList<>(List.of(hospitalTag1));

        BDDMockito.given(hospitalRepository.findById(any())).willReturn(Optional.ofNullable(hospital));
        BDDMockito.given(hospitalTagMappingRepository.findAllByHospitalId(any())).willReturn(hospitalTagMappings);
        BDDMockito.given(hospitalTagRepository.findAllByIdIn(any())).willReturn(hospitalTags);
        BDDMockito.given(appDataS3Client.getPresignedUrl(any())).willReturn(hospital.getImage());

        final HospitalDetailResponse expected = HospitalDetailResponse.of(
                "병원 이름",
                "병원 전화번호",
                new ArrayList<>(List.of("라벨1")),
                "병원 소개",
                "병원 주소",
                "병원 도로명주소",
                "병원 이미지"
        );

        //when
        final HospitalDetailResponse actual = hospitalService.getHospitalDetail(hospitalId);

        //then
        Assertions.assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
        Assertions.assertThat(actual.address()).isEqualTo("병원 주소");
    }
}

