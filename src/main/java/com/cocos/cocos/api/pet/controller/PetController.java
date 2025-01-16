package com.cocos.cocos.api.pet.controller;

import com.cocos.cocos.api.pet.dto.request.PetGenerationRequest;
import com.cocos.cocos.api.pet.service.PetService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/pets")
@RequiredArgsConstructor
public class PetController implements PetControllerSwagger {
    private static final Long memberId = 1L;
    private final PetService petService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> addPet(
            @RequestBody final PetGenerationRequest petGenerationRequest
    ) {
        petService.addPet(petGenerationRequest, memberId);
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }
}
