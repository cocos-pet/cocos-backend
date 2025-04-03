package com.cocos.cocos.api.location.controller;

import com.cocos.cocos.api.location.dto.response.LocationResponse;
import com.cocos.cocos.api.location.service.LocationService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/locations")
@RequiredArgsConstructor
public class LocationController implements LocationControllerSwagger {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<BaseResponse<LocationResponse>> getLocations() {
        return SuccessResponse.success(SuccessMessage.OK, locationService.getLocations());
    }
}
