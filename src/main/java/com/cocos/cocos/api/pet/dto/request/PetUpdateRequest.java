package com.cocos.cocos.api.pet.dto.request;

import com.cocos.cocos.enums.pet.Gender;
import com.cocos.cocos.validation.breed.BreedIdConstraint;
import com.cocos.cocos.validation.disease.DiseaseIdsConstraint;
import com.cocos.cocos.validation.symptom.SymptomIdsConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "반려동물 수정 형식")
public record PetUpdateRequest(
        @Schema(description = "동물 종 아이디", example = "1")
        @BreedIdConstraint
        Long breedId,

        @Schema(description = "반려동물 이름", example = "포리")
        String name,

        @Schema(description = "성별", example = "F or M")
        Gender gender,

        @Schema(description = "나이", example = "12")
        @Min(1)
        Integer age, // [TODO] 프론트 배포 후 제거 필요

        @Schema(description = "생년월일", example = "2020-01-01")
        LocalDate birthDate,

        @Schema(description = "질병 아이디 리스트", example = "[1,2,3]")
        @DiseaseIdsConstraint
        List<Long> diseaseIds,

        @Schema(description = "증상 아이디 리스트", example = "[1,2,3]")
        @SymptomIdsConstraint
        List<Long> symptomIds
) {
}
