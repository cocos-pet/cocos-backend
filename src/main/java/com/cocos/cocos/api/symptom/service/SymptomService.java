package com.cocos.cocos.api.symptom.service;

import com.cocos.cocos.api.symptom.dto.response.SymptomResponse;
import com.cocos.cocos.api.symptom.dto.response.SymptomsOfBodiesResponse;
import com.cocos.cocos.api.symptom.dto.response.SymptomsOfBodyResponse;
import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SymptomService {

    private final SymptomRepository symptomRepository;
    private final BodyRepository bodyRepository;

    @Transactional(readOnly = true)
    public SymptomsOfBodiesResponse getSymptoms(final List<Long> bodyIds) {
        if (bodyIds == null || bodyIds.isEmpty()) {
            return SymptomsOfBodiesResponse.of(List.of());
        }

        return SymptomsOfBodiesResponse.of(
                bodyIds.stream()
                        .map(bodyId -> {
                            final Body body = bodyRepository.findById(bodyId).orElseThrow(
                                    () -> new CocosException(FailMessage.NOT_FOUND_BODY)
                            );
                            final List<Symptom> symptoms = symptomRepository.findAllByBodyId(bodyId);
                            return SymptomsOfBodyResponse.of(bodyId, body.getName(),
                                    symptoms.stream()
                                            .map(symptom -> SymptomResponse.of(symptom.getId(), symptom.getName()))
                                            .toList()
                            );
                        })
                        .toList()
        );
    }
}
