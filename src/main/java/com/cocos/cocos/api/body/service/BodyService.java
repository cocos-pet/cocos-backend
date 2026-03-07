package com.cocos.cocos.api.body.service;

import com.cocos.cocos.api.body.dto.response.BodiesResponse;
import com.cocos.cocos.api.body.dto.response.BodyResponse;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.pet.PetProblem;
import com.cocos.cocos.external.s3.S3BucketType;
import com.cocos.cocos.external.s3.S3PresignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodyService {

    private final BodyRepository bodyRepository;
    private final DiseaseRepository diseaseRepository;
    private final SymptomRepository symptomRepository;
    private final S3PresignClient s3PresignClient;

    @Transactional(readOnly = true)
    public BodiesResponse getBodies(final PetProblem petProblem) {

        final List<Body> bodies = findBodiesByPetProblem(petProblem);
        return BodiesResponse.of(
                bodies.stream()
                        .map(body -> BodyResponse.of(body.getId(), body.getName(), s3PresignClient.get(S3BucketType.APP_DATA, body.getImage())))
                        .toList()
        );
    }

    private List<Body> findBodiesByPetProblem(final PetProblem petProblem) {
        if (petProblem == null) {
            return bodyRepository.findAll();
        } else {
            final List<Long> bodyIds = fetchBodyIdsByPetProblem(petProblem);
            return bodyRepository.findAllById(bodyIds);
        }
    }

    private List<Long> fetchBodyIdsByPetProblem(final PetProblem petProblem) {
        if (petProblem.equals(PetProblem.DISEASE)) {
            return diseaseRepository.findAll().stream()
                    .map(Disease::getBodyId)
                    .toList();
        } else {
            return symptomRepository.findAll().stream()
                    .map(Symptom::getBodyId)
                    .toList();
        }
    }
}
