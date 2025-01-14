package com.cocos.cocos.api.symptom.controller;

import com.cocos.cocos.api.symptom.dto.response.SymptomsOfBodiesResponse;
import com.cocos.cocos.api.symptom.service.SymptomService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/symptoms")
@RequiredArgsConstructor
public class SymptomController implements SymptomControllerSwagger {

    private final SymptomService symptomService;

    @GetMapping
    public ResponseEntity<BaseResponse<SymptomsOfBodiesResponse>> getSymptoms(
            @RequestParam(name = "bodyIds") final List<Long> bodyIds
    ) {
        return SuccessResponse.success(SuccessMessage.OK, symptomService.getSymptoms(bodyIds));
    }
}
