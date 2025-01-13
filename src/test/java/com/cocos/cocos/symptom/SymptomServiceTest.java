package com.cocos.cocos.symptom;

import com.cocos.cocos.api.symptom.dto.response.SymptomResponse;
import com.cocos.cocos.api.symptom.dto.response.SymptomsOfBodiesResponse;
import com.cocos.cocos.api.symptom.dto.response.SymptomsOfBodyResponse;
import com.cocos.cocos.api.symptom.service.SymptomService;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.db.symptom.entity.Symptom;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("증상 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SymptomServiceTest {

    @InjectMocks
    private SymptomService symptomService;

    @Mock
    private SymptomRepository symptomRepository;

    @Mock
    private BodyRepository bodyRepository;

    @Test
    @DisplayName("신체 부위 아이디에 해당하는 증상 리스트를 조회할 수 있다.")
    void getDiseases() {
        //given
        final Body body1 = Body.builder()
                .name("신체1")
                .image("이미지1")
                .build();
        final Body body2 = Body.builder()
                .name("신체1")
                .image("이미지1")
                .build();
        final List<Long> bodyIds = new ArrayList<>(List.of(1L, 2L));

        final Symptom symptom1 = Symptom.builder()
                .name("증상1")
                .bodyId(1L)
                .build();
        final Symptom symptom2 = Symptom.builder()
                .name("증상2")
                .bodyId(2L)
                .build();
        final List<Symptom> symptoms1 = new ArrayList<>(List.of(symptom1));
        final List<Symptom> sypmtoms2 = new ArrayList<>(List.of(symptom2));

        BDDMockito.given(bodyRepository.findById(1L)).willReturn(Optional.ofNullable(body1));
        BDDMockito.given(bodyRepository.findById(2L)).willReturn(Optional.ofNullable(body2));
        BDDMockito.given(symptomRepository.findAllByBodyId(1L)).willReturn(symptoms1);
        BDDMockito.given(symptomRepository.findAllByBodyId(2L)).willReturn(sypmtoms2);

        final SymptomsOfBodiesResponse expected = SymptomsOfBodiesResponse.of(
                bodyIds.stream()
                        .map(bodyId -> {
                            if (bodyId == 1L) {
                                return SymptomsOfBodyResponse.of(bodyId, body1.getName(),
                                        symptoms1.stream()
                                                .map(symptom -> SymptomResponse.of(symptom.getId(), symptom.getName()))
                                                .toList()
                                );
                            } else {
                                return SymptomsOfBodyResponse.of(bodyId, body2.getName(),
                                        sypmtoms2.stream()
                                                .map(symptom -> SymptomResponse.of(symptom.getId(), symptom.getName()))
                                                .toList()
                                );
                            }
                        })
                        .toList()
        );

        //when
        final SymptomsOfBodiesResponse actual = symptomService.getSymptoms(bodyIds);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
