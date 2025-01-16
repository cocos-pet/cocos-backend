package com.cocos.cocos.api.pet.service;

import com.cocos.cocos.api.pet.dto.request.PetGenerationRequest;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.entity.PetDisease;
import com.cocos.cocos.db.pet.entity.PetSymptom;
import com.cocos.cocos.db.pet.repository.PetDiseaseRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.pet.repository.PetSymptomRepository;
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

    @Transactional()
    public void addPet(final PetGenerationRequest petGenerationRequest, final Long memberId) {
        final Pet pet = petRepository.save(
                Pet.builder()
                        .name(petGenerationRequest.name())
                        .gender(petGenerationRequest.gender())
                        .age(petGenerationRequest.age())
                        .memberId(memberId)
                        .breedId(petGenerationRequest.breedId())
                        .build()
        );

        if (petGenerationRequest.diseaseIds() != null) {
            final List<PetDisease> petDiseases = petGenerationRequest.diseaseIds().stream()
                    .map(diseaseId -> new PetDisease(pet.getId(), diseaseId))
                    .toList();

            petDiseaseRepository.saveAll(petDiseases);
        }

        if ( petGenerationRequest.symptomIds() != null) {
            final List<PetSymptom> petSymptoms = petGenerationRequest.symptomIds().stream()
                    .map(symptomId -> new PetSymptom(pet.getId(), symptomId))
                    .toList();

            petSymptomRepository.saveAll(petSymptoms);
        }
    }
}
