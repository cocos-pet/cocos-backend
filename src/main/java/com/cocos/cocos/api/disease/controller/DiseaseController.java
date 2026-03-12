package com.cocos.cocos.api.disease.controller;

import com.cocos.cocos.api.disease.dto.response.DiseasesOfBodiesResponse;
import com.cocos.cocos.api.disease.service.DiseaseService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.validation.body.BodyIdsConstraint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/diseases")
@RequiredArgsConstructor
public class DiseaseController implements DiseaseControllerSwagger{

    private final DiseaseService diseaseService;

    @GetMapping
    public ResponseEntity<BaseResponse<DiseasesOfBodiesResponse>> getDiseases(
            @RequestParam(name = "bodyIds", required = false) @BodyIdsConstraint final List<Long> bodyIds
    ) {
        return SuccessResponse.success(SuccessMessage.OK, diseaseService.getDiseases(bodyIds));
    }
}
