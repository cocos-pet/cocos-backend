package com.cocos.cocos.api.breed.service;

import com.cocos.cocos.api.breed.dto.response.BreedResponse;
import com.cocos.cocos.api.breed.dto.response.BreedsResponse;
import com.cocos.cocos.db.breed.entity.Breed;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BreedService {

    private final BreedRepository breedRepository;

    @Transactional(readOnly = true)
    public BreedsResponse getBreeds(final String breedName, final Long animalId) {

        final List<Breed> allBreeds = getBreedsByBreedNameAndAnimalId(breedName, animalId);
        allBreeds.sort(Comparator.comparing(Breed::isEtc));

        return BreedsResponse.of(allBreeds.stream()
                .map(breed -> BreedResponse.of(breed.getId(), breed.getName()))
                .toList());
    }

    private List<Breed> getBreedsByBreedNameAndAnimalId(final String breedName, final Long animalId) {
        if (breedName == null || breedName.isEmpty()) {
            return breedRepository.findAllByAnimalId(animalId);
        } else {
            final List<Breed> breeds = breedRepository.findAllByNameContainingAndAnimalId(breedName, animalId);
            final boolean containsEtc = breeds.stream().anyMatch(Breed::isEtc);
            if (!containsEtc) {
                final Breed etcBreed = breedRepository.findByAnimalIdAndIsEtcTrue(animalId);
                if (etcBreed != null) {
                    breeds.add(etcBreed);
                }
            }
            return breeds;
        }
    }
}
