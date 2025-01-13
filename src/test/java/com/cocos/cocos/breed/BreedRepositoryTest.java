package com.cocos.cocos.breed;

import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("품종 레포지토리 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BreedRepositoryTest {

    @Autowired
    private BreedRepository breedRepository;

    @BeforeEach
    void setUp() {
        breedRepository.deleteAll();
    }

    @Test
    @DisplayName("이름이 포함된 품종 리스트를 가져올 수 있다.")
    void getBreedsContainingName() {
        //given
        final Breed breed1 = Breed.builder()
                .name("포")
                .animalId(1L)
                .build();
        final Breed breed2 = Breed.builder()
                .name("포메")
                .animalId(1L)
                .build();
        final Breed breed3 = Breed.builder()
                .name("포메라")
                .animalId(1L)
                .build();
        final Breed breed4 = Breed.builder()
                .name("포메라니")
                .animalId(1L)
                .build();
        final Breed breed5 = Breed.builder()
                .name("포메라니안")
                .animalId(1L)
                .build();
        final Breed breed6 = Breed.builder()
                .name("나는 고양이")
                .animalId(2L)
                .build();
        breedRepository.save(breed1);
        breedRepository.save(breed2);
        breedRepository.save(breed3);
        breedRepository.save(breed4);
        breedRepository.save(breed5);
        breedRepository.save(breed6);


        final String BREED_NAME1 = "라니";
        final String BREED_NAME2 = "라";
        final List<Breed> expected1 = new ArrayList<>(List.of(breed4, breed5));
        final List<Breed> expected2 = new ArrayList<>(List.of(breed3, breed4, breed5));

        //when
        final List<Breed> actual1 = breedRepository.findAllByNameContainingAndAnimalId(BREED_NAME1, 1L);
        final List<Breed> actual2 = breedRepository.findAllByNameContainingAndAnimalId(BREED_NAME2, 1L);

        //then
        Assertions.assertThat(actual1)
                .extracting(Breed::getName)
                .isEqualTo(expected1.stream()
                        .map(Breed::getName)
                        .toList());

        Assertions.assertThat(actual2)
                .extracting(Breed::getName)
                .isEqualTo(expected2.stream()
                        .map(Breed::getName)
                        .toList());

    }

}
