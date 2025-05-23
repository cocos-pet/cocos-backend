package com.cocos.cocos.api.pet.controller;

import com.cocos.cocos.api.pet.dto.response.PetOwnerCheckResponse;
import com.cocos.cocos.api.pet.dto.response.PetResponse;
import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
import com.cocos.cocos.api.pet.service.PetService;
import com.cocos.cocos.common.response.BaseResponse;
import com.cocos.cocos.common.response.SuccessResponse;
import com.cocos.cocos.enums.message.SuccessMessage;
import com.cocos.cocos.util.PrincipalHandler;
import com.cocos.cocos.validation.member.MemberNicknameConstraint;
import com.cocos.cocos.validation.pet.PetIdConstraint;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/pets")
@RequiredArgsConstructor
public class PetController implements PetControllerSwagger {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> addPet(
            @RequestBody @Valid final PetCreateRequest petCreateRequest
    ) {
        //ToDo: 넘길 때 DTO 자체 보단, 값을 넘기는 것이 Controller에서 사용하는 DTO의 역할을 잘 지키는 것이라고 생각함
        petService.addPet(petCreateRequest, PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.CREATED, null);
    }

    @PatchMapping("/{petId}")
    public ResponseEntity<BaseResponse<Void>> updatePet(
            @PathVariable(name = "petId") @PetIdConstraint final Long petId,
            @RequestBody @Valid final PetUpdateRequest petUpdateRequest
    ) {
        //ToDo: 넘길 때 DTO 자체 보단, 값을 넘기는 것이 Controller에서 사용하는 DTO의 역할을 잘 지키는 것이라고 생각함
        petService.updatePet(petUpdateRequest, petId, PrincipalHandler.getMemberIdFromPrincipal());
        return SuccessResponse.success(SuccessMessage.OK, null);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PetResponse>> getPet(
            @RequestParam(name = "nickname", required = false) @MemberNicknameConstraint final String nickname
    ) {
        return SuccessResponse.success(SuccessMessage.OK, petService.getPet(nickname, PrincipalHandler.getMemberIdFromPrincipal()));
    }

    @GetMapping("/owner/check")
    public ResponseEntity<BaseResponse<PetOwnerCheckResponse>> checkOwner() {
        return SuccessResponse.success(SuccessMessage.OK, petService.checkPetOwner(PrincipalHandler.getMemberIdFromPrincipal()));
    }

}
