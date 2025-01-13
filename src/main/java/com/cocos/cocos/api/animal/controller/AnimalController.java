package com.cocos.cocos.api.animal.controller;

import com.cocos.cocos.api.animal.dto.response.AnimalsResponse;
import com.cocos.cocos.api.animal.service.AnimalService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/animals")
@RequiredArgsConstructor
public class AnimalController implements AnimalControllerSwagger {

    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<BaseResponse<AnimalsResponse>> getAnimals() {
        return SuccessResponse.success(SuccessMessage.OK, animalService.getAnimals());
    }
}
