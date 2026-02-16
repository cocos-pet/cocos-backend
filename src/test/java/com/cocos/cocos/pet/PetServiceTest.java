package com.cocos.cocos.pet;

import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import com.cocos.cocos.api.pet.service.PetService;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.pet.entity.Pet;
import com.cocos.cocos.db.pet.repository.PetDiseaseRepository;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.db.pet.repository.PetSymptomRepository;
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

import java.time.LocalDate;
import java.util.List;

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

}
