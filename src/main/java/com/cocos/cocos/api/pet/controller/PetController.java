package com.cocos.cocos.api.pet.controller;

import com.cocos.cocos.api.pet.dto.reponse.PetResponse;
import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
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
            @RequestBody final PetCreateRequest petCreateRequest
    ) {
        petService.addPet(petCreateRequest, memberId);
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<BaseResponse<Void>> updatePet(
            @PathVariable(name = "petId") final Long petId,
            @RequestBody final PetUpdateRequest petUpdateRequest
    ) {
        petService.updatePet(petUpdateRequest, petId,  memberId);
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PetResponse>> getPet(
            @RequestParam(name = "nickname", required = false) final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK,  petService.getPet(nickname, memberId));
    }
}
