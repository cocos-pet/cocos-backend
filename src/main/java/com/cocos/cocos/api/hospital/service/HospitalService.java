package com.cocos.cocos.api.hospital.service;

import com.cocos.cocos.api.hospital.dto.response.*;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.district.entity.District;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.entity.HospitalTag;
import com.cocos.cocos.db.hospital.entity.HospitalTagMapping;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.db.hospital.repository.HospitalTagMappingRepository;
import com.cocos.cocos.db.hospital.repository.HospitalTagRepository;
import com.cocos.cocos.db.hospital.repository.HospitalVisitPurposeRepository;
import com.cocos.cocos.enums.hospital.HospitalSortCriteria;
import com.cocos.cocos.enums.location.LocationType;
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
    private final DistrictRepository districtRepository;
    private final HospitalTagRepository hospitalTagRepository;
    private final HospitalTagMappingRepository hospitalTagMappingRepository;
    private final HospitalVisitPurposeRepository hospitalVisitPurposeRepository;
    private final AppDataS3Client appDataS3Client;

    @Transactional(readOnly = true)
    public HospitalListResponse getHospitals(
            final Long locationId,
            final LocationType locationType,
            final Long cursorId,
            final int size,
            final String keyword,
            final HospitalSortCriteria hospitalSortCriteria,
            final Integer cursorReviewCount
    ) {
        final List<Long> districtIds = getDistrictIds(locationId, locationType);
        final List<Hospital> hospitals = getHospitalsByKeywordAndCursor(size, districtIds, keyword, cursorId, cursorReviewCount, hospitalSortCriteria);
        final Long nextCursorId = hospitals.isEmpty() ? null : hospitals.getLast().getId();
        final Integer nextCursorReviewCount = hospitals.isEmpty() ? null : hospitals.getLast().getReviewCount();

        return HospitalListResponse.of(
                nextCursorId,
                HospitalSortCriteria.REVIEW == hospitalSortCriteria ? nextCursorReviewCount : null,
                hospitals.stream().map(hospital -> HospitalResponse.of(
                        hospital.getId(),
                        hospital.getName(),
                        hospital.getDisplayAddress(),
                        hospital.getReviewCount(),
                        hospital.getImage()
                )).toList()
        );
    }

    private List<Long> getDistrictIds(final Long locationId, final LocationType locationType) {
        if (locationType == null) {
            return List.of();
        }

        return switch (locationType) {
            case LocationType.CITY ->
                    districtRepository.findByCityId(locationId).stream().map(District::getId).toList();
            case LocationType.DISTRICT -> List.of(locationId);
            default -> List.of();
        };
    }

    private List<Hospital> getHospitalsByKeywordAndCursor(final int size, final List<Long> districtIds, final String keyword, final Long cursorId, final Integer cursorReviewCount, final HospitalSortCriteria hospitalSortCriteria) {
        if (keyword != null && !keyword.isBlank()) {
            return getHospitalsByKeyword(size, districtIds, keyword, cursorId);
        } else {
            return getHospitalsBySortAndCursor(size, districtIds, cursorId, cursorReviewCount, hospitalSortCriteria);
        }
    }

    private List<Hospital> getHospitalsBySortAndCursor(int size, List<Long> districtIds, Long cursorId, Integer cursorReviewCount, HospitalSortCriteria hospitalSortCriteria) {
        // TODO: 유효한 정렬 기준 확인하는 로직으로 따로 분리 (정렬 기준 늘어날 경우)
        if (hospitalSortCriteria != HospitalSortCriteria.REVIEW) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_SORT_CRITERIA);
        }

        Pageable pageable = PageRequest.of(0, size, Sort.by(
                Sort.Order.desc(hospitalSortCriteria.getFieldName()),
                SortConstants.ID_DESC
        ));


        if (cursorId == null) {
            if (districtIds.isEmpty()) {
                return hospitalRepository.findAll(pageable).getContent();
            } else {
                return hospitalRepository.findAllByDistrictIdIn(districtIds, pageable);
            }
        } else {
            if (districtIds.isEmpty()) {
                return hospitalRepository.findAllWithCursor(cursorId, cursorReviewCount, pageable);
            } else {
                return hospitalRepository.findAllByDistrictIdInWithCursor(districtIds, cursorId, cursorReviewCount, pageable);
            }
        }

    }

    private List<Hospital> getHospitalsByKeyword(int size, List<Long> districtIds, String keyword, Long cursorId) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(
                SortConstants.ID_DESC
        ));
        if (cursorId == null) {
            if (districtIds.isEmpty()) {
                return hospitalRepository.findAllByNameContaining(keyword, pageable);
            } else {
                return hospitalRepository.findAllByNameContainingAndDistrictIdIn(keyword, districtIds, pageable);
            }
        } else {
            if (districtIds.isEmpty()) {
                return hospitalRepository.findAllByNameContainingAndIdLessThan(keyword, cursorId, pageable);
            } else {
                return hospitalRepository.findAllByNameContainingAndDistrictIdInAndIdLessThan(keyword, districtIds, cursorId, pageable);
            }
        }
    }

    @Transactional(readOnly = true)
    public HospitalDetailResponse getHospitalDetail(final Long hospitalId) {
        final Hospital hospital = hospitalRepository.findById(hospitalId).orElseThrow(
                () -> new CocosException(FailMessage.NOT_FOUND_HOSPITAL)
        );
        final List<Long> hospitalTagIds = getHospitalTagIds(hospitalId);
        final List<String> hospitalTags = getHospitalTagLabels(hospitalTagIds);

        return HospitalDetailResponse.of(
                hospital.getName(),
                hospital.getPhoneNumber(),
                hospitalTags,
                hospital.getIntroduction(),
                hospital.getDisplayAddress(),
                appDataS3Client.getPresignedUrl(hospital.getImage())
        );
    }

    private List<Long> getHospitalTagIds(final Long hospitalId) {
        return hospitalTagMappingRepository.findAllByHospitalId(hospitalId).stream()
                .map(HospitalTagMapping::getHospitalTagId)
                .toList();
    }

    private List<String> getHospitalTagLabels(final List<Long> hospitalTagIds) {
        return hospitalTagRepository.findAllByIdIn(hospitalTagIds).stream()
                .map(HospitalTag::getLabel)
                .toList();
    }

    @Transactional(readOnly = true)
    public HospitalVisitPurposeListResponse getHospitalVisitPurposeList() {
        return HospitalVisitPurposeListResponse.of(
                hospitalVisitPurposeRepository.findAll().stream()
                        .map(hospitalVisitPurpose -> HospitalVisitPurposeResponse.of(
                                hospitalVisitPurpose.getId(),
                                hospitalVisitPurpose.getName()
                        ))
                        .toList()
        );
    }
}
