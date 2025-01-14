package com.cocos.cocos.breed;

import com.cocos.cocos.api.breed.dto.response.BreedResponse;
import com.cocos.cocos.api.breed.dto.response.BreedsResponse;
import com.cocos.cocos.api.breed.service.BreedService;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
@DisplayName("품종 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BreedServiceTest {

    @InjectMocks
    private BreedService breedService;

    @Mock
    private BreedRepository breedRepository;

    @Test
    @DisplayName("품종 리스트를 조회할 수 있다.")
    void getBodies() {
        //given
        final String breedName = "라";
        final Long animalId = 1L;
        final Breed breed1 = Breed.builder()
                .name("포메라")
                .animalId(1L)
                .build();
        final Breed breed2 = Breed.builder()
                .name("포메라니")
                .animalId(1L)
                .build();
        final Breed breed3 = Breed.builder()
                .name("포메라니안")
                .animalId(1L)
                .build();

        final List<Breed> breeds = new ArrayList<>(List.of(breed1, breed2, breed3));
        BDDMockito.given(breedRepository.findAllByNameContainingAndAnimalId(any(), any())).willReturn(breeds);

        final BreedsResponse expected = BreedsResponse.of(
                breeds.stream()
                        .map(breed -> BreedResponse.of(breed.getId(), breed.getName()))
                        .toList()
        );

        //when
        final BreedsResponse actual = breedService.getBreeds(breedName, animalId);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
