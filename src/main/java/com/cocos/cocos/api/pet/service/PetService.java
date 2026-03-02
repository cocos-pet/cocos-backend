package com.cocos.cocos.api.pet.service;

import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
import com.cocos.cocos.api.pet.dto.response.PetDiseaseResponse;
import com.cocos.cocos.api.pet.dto.response.PetOwnerCheckResponse;
import com.cocos.cocos.api.pet.dto.response.PetResponse;
import com.cocos.cocos.api.pet.dto.response.PetSymptomResponse;
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
import com.cocos.cocos.db.pet.support.PetAgeResolver;
import com.cocos.cocos.db.pet.support.PetAgeResolver.AgeAndBirthDate;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.external.s3.S3BucketType;
import com.cocos.cocos.external.s3.S3PresignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
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
    private final S3PresignClient s3PresignClient;
    private final Clock clock;

    //ToDo: yml에 추가하는 방향 고민 중
    private static final String PET_BASE_IMAGE_URL = "member/basePetImage.png";

    //ToDo: Transactional 수정
    @Transactional()
    public void addPet(final PetCreateRequest petCreateRequest, final Long memberId) {
        if (petRepository.existsByMemberId(memberId)) {
            throw new CocosException(FailMessage.CONFLICT_PET);
        }

        AgeAndBirthDate reconciled =
                PetAgeResolver.resolve(
                        petCreateRequest.age(),
                        petCreateRequest.birthDate(),
                        clock
                );

        final Pet pet = petRepository.save(
                Pet.builder()
                        .name(petCreateRequest.name())
                        .gender(petCreateRequest.gender())
                        .image(PET_BASE_IMAGE_URL)
                        .age(reconciled.age())
                        .birthDate(reconciled.birthDate())
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

    private void validatePetDiseases(final List<Long> diseaseIds) {
        final long diseaseCount = diseaseRepository.countByIdIn(diseaseIds);
        if (diseaseCount != diseaseIds.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_DISEASE);
        }
    }

    private void validatePetSymptoms(final List<Long> symptomIds) {
        final long symptomCount = symptomRepository.countByIdIn(symptomIds);
        if (symptomCount != symptomIds.size()) {
            throw new CocosException(FailMessage.NOT_FOUND_SYMPTOM);
        }
    }

    private void savePetSymptoms(final List<Long> symptomIds, Long petId) {
        final List<PetSymptom> petSymptoms = symptomIds.stream()
                .map(symptomId -> PetSymptom.builder().petId(petId).symptomId(symptomId).build()).toList();

        petSymptomRepository.saveAll(petSymptoms);
    }

    private void savePetDiseases(final List<Long> diseaseIds, Long petId) {
        final List<PetDisease> petDiseases = diseaseIds.stream()
                .map(diseaseId -> PetDisease.builder().petId(petId).diseaseId(diseaseId).build()).toList();

        petDiseaseRepository.saveAll(petDiseases);
    }

    @Transactional
    public void updatePet(final PetUpdateRequest petUpdateRequest, final Long petId, final Long memberId) {
        final Pet pet = petRepository.findById(petId).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_PET));
        if (!pet.getMemberId().equals(memberId)) {
            throw new CocosException(FailMessage.FORBIDDEN_PET_UPDATE);
        }
        if (petUpdateRequest.breedId() != null && !breedRepository.existsById(petUpdateRequest.breedId())) {
            throw new CocosException(FailMessage.NOT_FOUND_BREED);
        }

        AgeAndBirthDate reconciled =
                PetAgeResolver.resolve(
                        petUpdateRequest.age(),
                        petUpdateRequest.birthDate(),
                        clock
                );

        pet.updateFields(petUpdateRequest.name(), petUpdateRequest.gender(), reconciled.age(), reconciled.birthDate(), petUpdateRequest.breedId());
        try {
            petRepository.flush();
        } catch (OptimisticLockingFailureException e) {
            throw new CocosException(FailMessage.CONFLICT_PET_UPDATE);
        }

        if (petUpdateRequest.diseaseIds() != null) {
            petDiseaseRepository.deleteAllByPetId(petId);
            petDiseaseRepository.flush();
            if (!petUpdateRequest.diseaseIds().isEmpty()) {
                validatePetDiseases(petUpdateRequest.diseaseIds());
                savePetDiseases(petUpdateRequest.diseaseIds(), pet.getId());
            }
        }

        if (petUpdateRequest.symptomIds() != null) {
            petSymptomRepository.deleteAllByPetId(petId);
            petSymptomRepository.flush();
            if (!petUpdateRequest.symptomIds().isEmpty()) {
                validatePetSymptoms(petUpdateRequest.symptomIds());
                savePetSymptoms(petUpdateRequest.symptomIds(), pet.getId());
            }
        }
    }

    @Transactional(readOnly = true)
    public PetResponse getPet(final String nickname, final Long memberId) {
        if (nickname != null && !memberRepository.existsByNickname(nickname)) {
            throw new CocosException(FailMessage.NOT_FOUND_MEMBER);
        }

        //ToDo: 메소드로 통일시켜도 좋을 것 같음(이전 다른 곳에서 사용되었던 코드와 통일 시키는 것이 좋아보임)
        final Long selectedMemberId = (nickname != null)
                ? memberRepository.findByNickname(nickname).orElseThrow(() -> new CocosException(FailMessage.NOT_FOUND_MEMBER)).getId()
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
                .map(petDisease ->
                        diseaseRepository.findById(petDisease.getDiseaseId()).orElseThrow(
                                () -> new CocosException(FailMessage.NOT_FOUND_DISEASE)
                        )
                ).toList();

        final List<PetSymptom> petSymptoms = petSymptomRepository.findAllByPetId(pet.getId());
        final List<Symptom> symptoms = petSymptoms.stream()
                .map(petSymptom ->
                        symptomRepository.findById(petSymptom.getSymptomId()).orElseThrow(
                                () -> new CocosException(FailMessage.NOT_FOUND_SYMPTOM)
                        )
                ).toList();

        return PetResponse.of(
                pet.getId(),
                s3PresignClient.get(S3BucketType.MEMBER_DATA, pet.getImage()),
                pet.getName(),
                pet.getAge(),
                pet.getBirthDate(),
                pet.getGender(),
                breed.getId(),
                breed.getName(),
                animal.getId(),
                animal.getName(),
                diseases.stream().map(disease -> PetDiseaseResponse.of(disease.getId(), disease.getName())).toList(),
                symptoms.stream().map(symptom -> PetSymptomResponse.of(symptom.getId(), symptom.getName())).toList()
        );
    }

    @Transactional(readOnly = true)
    public PetOwnerCheckResponse checkPetOwner(final Long memberId) {
        return PetOwnerCheckResponse.of(
                petRepository.existsByMemberId(memberId)
        );
    }
}
