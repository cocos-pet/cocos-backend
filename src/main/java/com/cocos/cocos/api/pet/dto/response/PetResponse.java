package com.cocos.cocos.api.pet.dto.response;

import com.cocos.cocos.enums.pet.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record PetResponse(
        @Schema(description = "반려동물 아이디", example = "1")
        Long petId,
        @Schema(description = "반려동물 이미지", example = "url")
        String petImage,
        @Schema(description = "반려동물 이름", example = "펫이름")
        String petName,
        @Schema(description = "반려동물 나이", example = "12")
        Integer petAge,
        @Schema(description = "반려동물 생년월일", example = "2022-02-13")
        LocalDate petBirthDate,
        @Schema(description = "반려동물 성별", example = "M or F")
        Gender petGender,
        @Schema(description = "종 아이디", example = "1")
        Long breedId,
        @Schema(description = "종 이름", example = "포메라니안")
        String breed,
        @Schema(description = "동물 아이디", example = "1")
        Long animalId,
        @Schema(description = "동물 이름", example = "강아지")
        String animal,
        @Schema(description = "질병 리스트")
        List<PetDiseaseResponse> diseases,
        @Schema(description = "증상 리스트")
        List<PetSymptomResponse> symptoms
) {
    public static PetResponse of(final Long petId, final String petImage, final String petName, final int petAge, final LocalDate petBirtDate, final Gender petGender, final Long breedId, final String breed, final Long animalId, final String animal, final List<PetDiseaseResponse> diseases, final List<PetSymptomResponse> symptoms) {
        return new PetResponse(petId, petImage, petName, petAge, petBirtDate, petGender, breedId, breed, animalId, animal, diseases, symptoms);
    }
}
