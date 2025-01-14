package com.cocos.cocos.api.disease.service;

import com.cocos.cocos.api.disease.dto.response.DiseaseResponse;
import com.cocos.cocos.api.disease.dto.response.DiseasesOfBodiesResponse;
import com.cocos.cocos.api.disease.dto.response.DiseasesOfBodyResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.enums.message.FailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final BodyRepository bodyRepository;

    @Transactional(readOnly = true)
    public DiseasesOfBodiesResponse getDiseases(final List<Long> bodyIds) {
        return DiseasesOfBodiesResponse.of(
                bodyIds.stream()
                        .map(bodyId -> {
                            final Body body = bodyRepository.findById(bodyId).orElseThrow(
                                    () -> new CocosException(FailMessage.NOT_FOUND_BODY)
                            );
                            final List<Disease> diseases = diseaseRepository.findAllByBodyId(bodyId);
                            return DiseasesOfBodyResponse.of(bodyId, body.getName(),
                                    diseases.stream()
                                            .map(disease -> DiseaseResponse.of(disease.getId(), disease.getName()))
                                            .toList()
                            );
                        })
                        .toList()
        );
    }
}
