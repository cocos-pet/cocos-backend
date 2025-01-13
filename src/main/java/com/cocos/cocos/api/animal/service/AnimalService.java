package com.cocos.cocos.api.animal.service;

import com.cocos.cocos.api.animal.dto.response.AnimalResponse;
import com.cocos.cocos.api.animal.dto.response.AnimalsResponse;
import com.cocos.cocos.db.animal.entity.Animal;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.external.AppDataS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final AppDataS3Client appDataS3Client;

    @Transactional(readOnly = true)
    public AnimalsResponse getAnimals() {
        final List<Animal> animals = animalRepository.findAll();
        return AnimalsResponse.of(
                animals.stream()
                        .map(animal -> AnimalResponse.of(animal.getId(), animal.getName(), appDataS3Client.getPresignedUrl(animal.getImage())))
                        .toList()
        );
    }
}
