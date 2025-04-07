package com.cocos.cocos.api.hospital.controller;

import com.cocos.cocos.api.hospital.dto.request.HospitalListRequest;
import com.cocos.cocos.api.hospital.dto.response.HospitalListResponse;
import com.cocos.cocos.api.hospital.service.HospitalService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/hospitals")
@RequiredArgsConstructor
public class HospitalController implements HospitalControllerSwagger {

    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<BaseResponse<HospitalListResponse>> getHospitals(
            @Valid @RequestBody HospitalListRequest hospitalListRequest) {
        return SuccessResponse.success(SuccessMessage.OK, hospitalService.getHospitals(hospitalListRequest.townId(), hospitalListRequest.cursorId(), hospitalListRequest.size(), hospitalListRequest.keyword(), hospitalListRequest.sortBy(), hospitalListRequest.cursorReviewCount()));
    }
}
