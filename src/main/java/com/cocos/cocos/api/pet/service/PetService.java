package com.cocos.cocos.api.pet.service;

import com.cocos.cocos.api.pet.dto.reponse.PetDiseaseResponse;
import com.cocos.cocos.api.pet.dto.reponse.PetResponse;
import com.cocos.cocos.api.pet.dto.reponse.PetSymptomResponse;
import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.animal.entity.Animal;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.member.repository.MemberRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.entity.PetDisease;
import com.cocos.cocos.db.pet.entity.PetSymptom;
import com.cocos.cocos.db.pet.repository.PetDiseaseRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.pet.repository.PetSymptomRepository;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.AppDataS3Client;
import com.cocos.cocos.external.MemberDataS3Client;
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
    private final AnimalRepository animalRepository;
    private final DiseaseRepository diseaseRepository;
    private final SymptomRepository symptomRepository;
    private final MemberRepository memberRepository;
    private final AppDataS3Client appDataS3Client;
    private final MemberDataS3Client memberDataS3Client;

    private static final String PET_BASE_IMAGE_URL = "member/basePetImage.png";

    @Transactional()
    public void addPet(final PetCreateRequest petCreateRequest, final Long memberId) {
        if (petRepository.existsByMemberId(memberId)) {
            throw new CocosException(FailMessage.CONFLICT_PET);
        }

        final Pet pet = petRepository.save(
                Pet.builder()
                        .name(petCreateRequest.name())
                        .gender(petCreateRequest.gender())
                        .image(PET_BASE_IMAGE_URL)
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
        List<Long> validDiseaseIds = diseaseRepository.findByIdIn(diseaseIds).stream()
                .map(Disease::getId)
                .toList();

        List<Long> invalidDiseaseIds = diseaseIds.stream()
                .filter(diseaseId -> !validDiseaseIds.contains(diseaseId))
                .toList();

        if (!invalidDiseaseIds.isEmpty()) {
            throw new CocosException(FailMessage.NOT_FOUND_DISEASE);
        }
    }

    private void validatePetSymptoms(List<Long> symptomIds) {
        List<Long> validSymptomIds = symptomRepository.findByIdIn(symptomIds).stream()
                .map(Symptom::getId)
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
        final List<PetDisease> petDiseases = diseaseIds.stream()
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
        if (petUpdateRequest.breedId() != null) {
            if (!breedRepository.existsById(petUpdateRequest.breedId())) {
                throw new CocosException(FailMessage.NOT_FOUND_BREED);
            }
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

    public PetResponse getPet(final String nickname, final Long memberId) {
        if (nickname != null && !memberRepository.existsByNickname(nickname)) {
                throw new CocosException(FailMessage.NOT_FOUND_MEMBER);
            }

        final Long selectedMemberId = (nickname != null)
                ? memberRepository.findByNickname(nickname).getId()
                : memberId;

        final Pet pet = petRepository.findByMemberId(selectedMemberId);

        if (pet == null) {
            throw new CocosException(FailMessage.NOT_FOUND_PET);
        }

        final Breed breed = breedRepository.findById(pet.getBreedId())
                .orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_BREED));

        final Animal animal = animalRepository.findById(breed.getAnimalId())
                .orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_ANIMAL));


        final List<PetDisease> petDiseases = petDiseaseRepository.findAllByPetId(pet.getId());
        final List<Disease> diseases = petDiseases.stream()
                .map(petDisease -> {
                    return diseaseRepository.findById(petDisease.getDiseaseId()).orElseThrow(
                            () -> new CocosException(FailMessage.NOT_FOUND_DISEASE)
                    );
                }).toList();

        final List<PetSymptom> petSymptoms = petSymptomRepository.findAllByPetId(pet.getId());
        final List<Symptom> symptoms = petSymptoms.stream()
                .map(petSymptom -> {
                    return symptomRepository.findById(petSymptom.getSymptomId()).orElseThrow(
                            () -> new CocosException(FailMessage.NOT_FOUND_SYMPTOM)
                    );
                }).toList();

        return PetResponse.of(
                pet.getId(),
                memberDataS3Client.getPresignedUrl(pet.getImage()),
                pet.getName(),
                pet.getAge(),
                pet.getGender(),
                breed.getId(),
                breed.getName(),
                animal.getId(),
                animal.getName(),
                diseases.stream().map(disease -> PetDiseaseResponse.of(disease.getId(), disease.getName())).toList(),
                symptoms.stream().map(symptom -> PetSymptomResponse.of(symptom.getId(), symptom.getName())).toList()
        );
    }
}
