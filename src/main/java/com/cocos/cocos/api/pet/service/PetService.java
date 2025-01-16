package com.cocos.cocos.api.pet.service;

import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.entity.PetDisease;
import com.cocos.cocos.db.pet.entity.PetSymptom;
import com.cocos.cocos.db.pet.repository.PetDiseaseRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.pet.repository.PetSymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final PetSymptomRepository petSymptomRepository;
    private final PetDiseaseRepository petDiseaseRepository;
    private final BreedRepository breedRepository;

    @Transactional()
    public void addPet(final PetCreateRequest petCreateRequest, final Long memberId) {
        if (!breedRepository.existsById(petCreateRequest.breedId())) {
            throw new CocosException(FailMessage.NOT_FOUND_BREED);
        }

        final Pet pet = petRepository.save(
                Pet.builder()
                        .name(petCreateRequest.name())
                        .gender(petCreateRequest.gender())
                        .age(petCreateRequest.age())
                        .memberId(memberId)
                        .breedId(petCreateRequest.breedId())
                        .build()
        );

        if (petCreateRequest.diseaseIds() != null) {
            validatePetDiseases(petCreateRequest.diseaseIds());
            savePetDiseases(petCreateRequest.diseaseIds(), pet.getId());
        }

        if (petCreateRequest.symptomIds() != null) {
            validatePetSymptoms(petCreateRequest.symptomIds());
            savePetSymptoms(petCreateRequest.symptomIds(), pet.getId());
        }
    }

    private void validatePetDiseases(List<Long> diseaseIds) {
        List<Long> validDiseaseIds = petDiseaseRepository.findAllById(diseaseIds).stream()
                .map(PetDisease::getId)
                .toList();

        List<Long> invalidDiseaseIds = diseaseIds.stream()
                .filter(diseaseId -> !validDiseaseIds.contains(diseaseId))
                .toList();

        if (!invalidDiseaseIds.isEmpty()) {
            throw new CocosException(FailMessage.NOT_FOUND_DISEASE);
        }
    }

    private void validatePetSymptoms(List<Long> symptomIds) {
        List<Long> validSymptomIds = petSymptomRepository.findAllById(symptomIds).stream()
                .map(PetSymptom::getId)
                .toList();

        List<Long> invalidSymptomIds = symptomIds.stream()
                .filter(symptomId -> !validSymptomIds.contains(symptomId))
                .toList();

        if (!invalidSymptomIds.isEmpty()) {
            throw new CocosException(FailMessage.NOT_FOUND_SYMPTOM);
        }
    }

    private void savePetSymptoms(final List<Long> symptomIds, Long petId) {
        final List<PetSymptom> petSymptoms = symptomIds.stream()
                .map(symptomId -> new PetSymptom(petId, symptomId))
                .toList();

        petSymptomRepository.saveAll(petSymptoms);
    }

    private void savePetDiseases(final List<Long> diseaseIds, Long petId) {
        final List<PetDisease> petDiseases =diseaseIds.stream()
                .map(diseaseId -> new PetDisease(petId, diseaseId))
                .toList();

        petDiseaseRepository.saveAll(petDiseases);
    }

    @Transactional
    public void updatePet(final PetUpdateRequest petUpdateRequest, final Long petId, final Long memberId) {
        final Pet pet = petRepository.findById(petId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_PET));
        if (!pet.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_PET_UPDATE);
        }
        if (!breedRepository.existsById(petUpdateRequest.breedId())) {
            throw new CocosException(FailMessage.NOT_FOUND_BREED);
        }
        pet.updateFields(petUpdateRequest.name(), petUpdateRequest.gender(), petUpdateRequest.age(), petUpdateRequest.breedId());

        if (petUpdateRequest.diseaseIds() != null) {
            petDiseaseRepository.deleteAllByPetId(petId);
            if (!petUpdateRequest.diseaseIds().isEmpty()) {
                validatePetDiseases(petUpdateRequest.diseaseIds());
                savePetDiseases(petUpdateRequest.diseaseIds(), pet.getId());
            }
        }

        if (petUpdateRequest.symptomIds() != null) {
            petSymptomRepository.deleteAllByPetId(petId);
            if (!petUpdateRequest.symptomIds().isEmpty()) {
                validatePetSymptoms(petUpdateRequest.symptomIds());
                savePetSymptoms(petUpdateRequest.symptomIds(), pet.getId());
            }
        }
    }
}
