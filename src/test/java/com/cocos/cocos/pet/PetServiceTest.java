package com.cocos.cocos.pet;

import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.dto.request.PetUpdateRequest;
import com.cocos.cocos.api.pet.service.PetService;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.repository.PetDiseaseRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.pet.repository.PetSymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import com.cocos.cocos.enums.pet.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("반려동물 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PetServiceTest {

    @InjectMocks
    PetService petService;

    @Mock
    PetRepository petRepository;

    @Mock
   PetSymptomRepository petSymptomRepository;

    @Mock
    PetDiseaseRepository petDiseaseRepository;

    @Mock
    DiseaseRepository diseaseRepository;

    @Mock
    BreedRepository breedRepository;

    @Test
    @DisplayName("반려동물을 추가할 수 있다.")
    void addPet() {
        // given
        final Long memberId = 1L;
        final Long breedId = 1L;
        final PetCreateRequest petCreateRequest = new PetCreateRequest(
                1L,
                "포리",
                Gender.M,
                12,
                LocalDate.parse("2014-01-01"),
                List.of(1L,2L,3L),
                null
        );

        final Pet pet = Pet.builder()
                .name("포리")
                .gender(Gender.M)
                .age(12)
                .birthDate(LocalDate.parse("2014-01-01"))
                .memberId(memberId)
                .breedId(breedId)
                .build();

        BDDMockito.given(diseaseRepository.countByIdIn(petCreateRequest.diseaseIds())).willReturn((long) petCreateRequest.diseaseIds().size());
        BDDMockito.given(petRepository.save(any(Pet.class))).willReturn(pet);

        // when
        petService.addPet(petCreateRequest, memberId);

        // then
        BDDMockito.verify(petRepository, times(1)).save(any(Pet.class));
        BDDMockito.verify(petDiseaseRepository, times(1)).saveAll(anyList());
        BDDMockito.verify(petSymptomRepository, times(0)).saveAll(anyList());
    }

    @Test
    @DisplayName("반려동물 수정 중 낙관락 충돌이 발생하면 CONFLICT_PET_UPDATE 예외를 반환한다.")
    void updatePet_optimisticLockConflict() {
        // given
        final Long petId = 1L;
        final Long memberId = 10L;
        final Pet pet = Pet.builder()
                .name("포리")
                .gender(Gender.M)
                .age(5)
                .birthDate(LocalDate.parse("2020-01-01"))
                .memberId(memberId)
                .breedId(1L)
                .image("member/basePetImage.png")
                .build();

        final PetUpdateRequest request = new PetUpdateRequest(
                null,
                "포리2",
                Gender.F,
                5,
                LocalDate.parse("2020-01-01"),
                null,
                null
        );

        BDDMockito.given(petRepository.findById(petId)).willReturn(Optional.of(pet));
        BDDMockito.willThrow(new ObjectOptimisticLockingFailureException(Pet.class, petId))
                .given(petRepository).flush();

        // when & then
        assertThatThrownBy(() -> petService.updatePet(request, petId, memberId))
                .isInstanceOf(CocosException.class)
                .extracting("failMessage")
                .isEqualTo(FailMessage.CONFLICT_PET_UPDATE);
    }

    @Test
    @DisplayName("반려동물 수정이 성공하면 flush가 호출되고 예외가 발생하지 않는다.")
    void updatePet_success() {
        // given
        final Long petId = 1L;
        final Long memberId = 10L;
        final Pet pet = Pet.builder()
                .name("포리")
                .gender(Gender.M)
                .age(5)
                .birthDate(LocalDate.parse("2020-01-01"))
                .memberId(memberId)
                .breedId(1L)
                .image("member/basePetImage.png")
                .build();

        final PetUpdateRequest request = new PetUpdateRequest(
                null,
                "포리2",
                Gender.F,
                5,
                LocalDate.parse("2020-01-01"),
                null,
                null
        );

        BDDMockito.given(petRepository.findById(petId)).willReturn(Optional.of(pet));

        // when & then
        assertThatCode(() -> petService.updatePet(request, petId, memberId))
                .doesNotThrowAnyException();
        BDDMockito.verify(petRepository, times(1)).flush();
        assertThat(pet.getName()).isEqualTo("포리2");
        assertThat(pet.getGender()).isEqualTo(Gender.F);
    }

}
