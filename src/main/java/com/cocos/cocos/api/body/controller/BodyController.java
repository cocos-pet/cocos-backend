package com.cocos.cocos.api.body.controller;

import com.cocos.cocos.api.body.dto.response.BodiesResponse;
import com.cocos.cocos.api.body.service.BodyService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/bodies")
@RequiredArgsConstructor
public class BodyController implements BodyControllerSwagger {

    private final BodyService bodyService;

    @GetMapping
    public ResponseEntity<BaseResponse<BodiesResponse>> getBodies() {
        return SuccessResponse.success(SuccessMessage.OK, bodyService.getBodies());
    }
}
