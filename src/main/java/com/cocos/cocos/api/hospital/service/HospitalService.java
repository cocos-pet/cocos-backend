package com.cocos.cocos.api.hospital.service;

import com.cocos.cocos.api.hospital.dto.response.HospitalListResponse;
import com.cocos.cocos.api.hospital.dto.response.HospitalResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.district.entity.District;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.enums.hospital.HospitalSortCriteria;
import com.cocos.cocos.enums.location.LocationType;
import com.cocos.cocos.enums.message.FailMessage;
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
        final List<Long> locationIds = getLocationIds(locationId, locationType);
        final List<Hospital> hospitals = getHospitalsByKeywordAndCursor(size, locationIds, keyword, cursorId, cursorReviewCount, hospitalSortCriteria);
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

    private List<Long> getLocationIds(final Long locationId, final LocationType locationType) {
        if (locationType == LocationType.CITY) {
            return districtRepository.findByCityId(locationId).stream().map(District::getId).toList();
        }
        return List.of(locationId);
    }

    private List<Hospital> getHospitalsByKeywordAndCursor(final int size, final List<Long> locationIds, final String keyword, final Long cursorId, final Integer cursorReviewCount, final HospitalSortCriteria hospitalSortCriteria) {
        if (keyword != null && !keyword.isBlank()) {
            Pageable pageable = PageRequest.of(0, size, Sort.by(
                    SortConstants.ID_DESC
            ));
            return (cursorId != null) ? hospitalRepository.findAllByNameContainingAndLocationIdInAndIdLessThan(keyword, locationIds, cursorId, pageable) : hospitalRepository.findAllByNameContainingAndLocationIdIn(keyword, locationIds, pageable);
        } else {
            Pageable pageable = PageRequest.of(0, size, Sort.by(
                    Sort.Order.desc(hospitalSortCriteria.getFieldName()),
                    SortConstants.ID_DESC
            ));
            if (hospitalSortCriteria == HospitalSortCriteria.REVIEW) {
                if (cursorId == null) {
                    return hospitalRepository.findAllByLocationIdIn(locationIds, pageable);
                } else {
                    return hospitalRepository.findAllByLocationIdInWithCursor(locationIds, cursorId, cursorReviewCount, pageable);
                }
            }
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_SORT_CRITERIA);
        }
    }
}
