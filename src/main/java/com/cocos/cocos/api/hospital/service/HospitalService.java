package com.cocos.cocos.api.hospital.service;

import com.cocos.cocos.api.hospital.dto.response.HospitalListResponse;
import com.cocos.cocos.api.hospital.dto.response.HospitalResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.hospital.entity.Hospital;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.enums.hospital.HospitalSortCriteria;
import com.cocos.cocos.enums.message.FailMessage;
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

    @Transactional(readOnly = true)
    public HospitalListResponse getHospitals(
            final Long townId,
            final Long cursorId,
            final int size,
            final String keyword,
            final HospitalSortCriteria hospitalSortCriteria,
            final Integer cursorReviewCount
    ) {
        List<Hospital> hospitals = getHospitalsByKeywordAndCursor(size, townId, keyword, cursorId, cursorReviewCount, hospitalSortCriteria);
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

    private List<Hospital> getHospitalsByKeywordAndCursor(int size, Long townId, String keyword, Long cursorId, Integer cursorReviewCount, HospitalSortCriteria hospitalSortCriteria) {
        if (keyword != null && !keyword.isBlank()) {
            Pageable pageable = PageRequest.of(0, size, Sort.by(
                    Sort.Order.desc("id")
            ));
            return (cursorId != null) ? hospitalRepository.findAllByNameContainingAndTownIdAndIdLessThan(keyword, townId, cursorId, pageable) : hospitalRepository.findAllByNameContainingAndTownId(keyword, townId, pageable);
        } else {
            Pageable pageable = PageRequest.of(0, size, Sort.by(
                    Sort.Order.desc(hospitalSortCriteria.getFieldName()),
                    Sort.Order.desc("id")
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
}
