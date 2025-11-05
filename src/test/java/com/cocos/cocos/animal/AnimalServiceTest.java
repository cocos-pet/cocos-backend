package com.cocos.cocos.animal;

import com.cocos.cocos.api.animal.dto.response.AnimalResponse;
import com.cocos.cocos.api.animal.dto.response.AnimalsResponse;
import com.cocos.cocos.api.animal.service.AnimalService;
import com.cocos.cocos.db.animal.entity.Animal;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.external.CloudfrontClient;
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
@DisplayName("동물 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AnimalServiceTest {

    @InjectMocks
    private AnimalService animalService;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private CloudfrontClient cloudfrontClient;

    @Test
    @DisplayName("동물 리스트를 조회할 수 있다.")
    void addPostLike() {
        //given
        final Animal animal1 = Animal.builder()
                .name("이름1")
                .image("image1")
                .build();
        final Animal animal2 = Animal.builder()
                .name("이름2")
                .image("image2")
                .build();
        final List<Animal> animals = new ArrayList<Animal>(List.of(animal1, animal2));
        final AnimalsResponse expected = AnimalsResponse.of(
                animals.stream()
                        .map(animal -> AnimalResponse.of(animal.getId(), animal.getName(), "image"))
                        .toList()
        );
        BDDMockito.given(animalRepository.findAll()).willReturn(animals);
        BDDMockito.given(cloudfrontClient.getAppCloudfrontUrl(any())).willReturn("image");

        //when
        final AnimalsResponse actual = animalService.getAnimals();

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
