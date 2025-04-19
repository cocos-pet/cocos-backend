package com.cocos.cocos.api.hospital.service;

import com.cocos.cocos.api.hospital.dto.response.HospitalDetailResponse;
import com.cocos.cocos.api.hospital.dto.response.HospitalListResponse;
import com.cocos.cocos.api.hospital.dto.response.HospitalResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.entity.HospitalTag;
import com.cocos.cocos.db.hospital.entity.HospitalTagMapping;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.db.hospital.repository.HospitalTagMappingRepository;
import com.cocos.cocos.db.hospital.repository.HospitalTagRepository;
import com.cocos.cocos.enums.hospital.HospitalSortCriteria;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.AppDataS3Client;
import com.cocos.cocos.util.SortConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalTagRepository hospitalTagRepository;
    private final HospitalTagMappingRepository hospitalTagMappingRepository;
    private final AppDataS3Client appDataS3Client;

    @Transactional(readOnly = true)
    public HospitalListResponse getHospitals(
            final Long townId,
            final Long cursorId,
            final int size,
            final String keyword,
            final HospitalSortCriteria hospitalSortCriteria,
            final Integer cursorReviewCount
    ) {
        final List<Hospital> hospitals = getHospitalsByKeywordAndCursor(size, townId, keyword, cursorId, cursorReviewCount, hospitalSortCriteria);
        final Long nextCursorId = hospitals.isEmpty() ? null : hospitals.getLast().getId();
        final Integer nextCursorReviewCount = hospitals.isEmpty() ? null : hospitals.getLast().getReviewCount();

        return HospitalListResponse.of(
                nextCursorId,
                HospitalSortCriteria.REVIEW == hospitalSortCriteria ? nextCursorReviewCount : null,
                hospitals.stream().map(hospital -> HospitalResponse.of(
                        hospital.getId(),
                        hospital.getName(),
                        hospital.getAddress(),
                        hospital.getReviewCount(),
                        hospital.getImage()
                )).toList()
        );
    }

    private List<Hospital> getHospitalsByKeywordAndCursor(final int size, final Long townId, final String keyword, final Long cursorId, final Integer cursorReviewCount, final HospitalSortCriteria hospitalSortCriteria) {
        if (keyword != null && !keyword.isBlank()) {
            Pageable pageable = PageRequest.of(0, size, Sort.by(
                    SortConstants.ID_DESC
            ));
            return (cursorId != null) ? hospitalRepository.findAllByNameContainingAndTownIdAndIdLessThan(keyword, townId, cursorId, pageable) : hospitalRepository.findAllByNameContainingAndTownId(keyword, townId, pageable);
        } else {
            Pageable pageable = PageRequest.of(0, size, Sort.by(
                    Sort.Order.desc(hospitalSortCriteria.getFieldName()),
                    SortConstants.ID_DESC
            ));
            if (hospitalSortCriteria == HospitalSortCriteria.REVIEW) {
                if (cursorId == null) {
                    return hospitalRepository.findAllByTownId(townId, pageable);
                } else {
                    return hospitalRepository.findAllByTownIdWithCursor(townId, cursorId, cursorReviewCount, pageable);
                }
            }
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_SORT_CRITERIA);
        }
    }

    @Transactional(readOnly = true)
    public HospitalDetailResponse getHospitalDetail(final Long hospitalId) {
        final Hospital hospital = hospitalRepository.findById(hospitalId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_HOSPITAL)
        );
        final List<Long> hospitalTagIds = getHospitalTagIds(hospitalId);
        final List<String> hospitalTags = hospitalTagRepository.findAllByIdIn(hospitalTagIds).stream()
                .map(HospitalTag::getLabel)
                .toList();

        return HospitalDetailResponse.of(
                hospital.getName(),
                hospital.getPhoneNumber(),
                hospitalTags,
                hospital.getIntroduction(),
                hospital.getAddress(),
                hospital.getRoadAddress(),
                appDataS3Client.getPresignedUrl(hospital.getImage())
        );
    }

    private List<Long> getHospitalTagIds(final Long hospitalId) {
        return hospitalTagMappingRepository.findAllByHospitalId(hospitalId).stream()
                .map(HospitalTagMapping::getHospitalTagId)
                .toList();
    }
}
