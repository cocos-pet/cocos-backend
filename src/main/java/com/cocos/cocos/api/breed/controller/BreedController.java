package com.cocos.cocos.api.breed.controller;

import com.cocos.cocos.api.breed.dto.response.BreedsResponse;
import com.cocos.cocos.api.breed.service.BreedService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/breeds")
@RequiredArgsConstructor
public class BreedController implements BreedControllerSwagger {

    private final BreedService breedService;

    @GetMapping("/{animalId}")
    public ResponseEntity<BaseResponse<BreedsResponse>> getBreeds(
            @RequestParam(name = "breedName", required = false) final String breedName,
            @PathVariable(name = "animalId") final Long animalId
    ) {
        return SuccessResponse.success(SuccessMessage.OK, breedService.getBreeds(breedName, animalId));
    }
}
