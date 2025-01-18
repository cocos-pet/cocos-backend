package com.cocos.cocos.api.breed.controller;

import com.cocos.cocos.api.breed.dto.response.BreedsResponse;
import com.cocos.cocos.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Breed Controller", description = "품종 관련 API")
public interface BreedControllerSwagger {

    @Operation(summary = "품종 리스트 조회 API", description = "품종 리스트를 조회하는 API입니다.")
    @ApiResponse(
            responseCode = "200",
            description = "품종 리스트 조회 성공")
    @Parameter(name = "breedName", description = "품종 이름", in = ParameterIn.QUERY, required = false, schema = @Schema(type = "String"))
    @Parameter(name = "animalId", description = "동물 아이디", in = ParameterIn.PATH, required = true, schema = @Schema(type = "Long"))
    public ResponseEntity<BaseResponse<BreedsResponse>> getBreeds(
            final String breedName,
            final Long animalId
    );
}
