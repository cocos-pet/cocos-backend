package com.cocos.cocos.api.breed.service;

import com.cocos.cocos.api.breed.dto.response.BreedResponse;
import com.cocos.cocos.api.breed.dto.response.BreedsResponse;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;

    @Transactional(readOnly = true)
    public BreedsResponse getBreeds(final String breedName, final Long animalId) {
        final List<Breed> breeds = breedRepository.findAllByNameContainingAndAnimalId(breedName, animalId);
        return BreedsResponse.of(breeds.stream()
                .map(breed -> BreedResponse.of(breed.getId(), breed.getName()))
                .toList()
        );
    }
}
