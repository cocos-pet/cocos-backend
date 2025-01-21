package com.cocos.cocos.api.pet.dto.request;

import com.cocos.cocos.enums.pet.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(description = "애완동물 수정 형식")
public record PetUpdateRequest(
        @Schema(description = "동물 종 아이디", example = "1")
        Long breedId,

        @Schema(description = "반려동물 이름", example = "포리")
        String name,

        @Schema(description = "성별", example = "F or M")
        Gender gender,

        @Schema(description = "나이", example = "12")
        Integer age,

        @Schema(description = "질병 아이디 리스트", example = "[1,2,3]")
        List<Long> diseaseIds,

        @Schema(description = "증상 아이디 리스트", example = "[1,2,3]")
        List<Long> symptomIds
) {
    public static PetUpdateRequest of(final Long breedId, final String name, final Gender gender, final Integer age, final List<Long> diseaseIds, final List<Long> symptomIds) {
        return new PetUpdateRequest(breedId, name, gender, age, diseaseIds, symptomIds);
    }
}
