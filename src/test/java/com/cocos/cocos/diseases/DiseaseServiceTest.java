package com.cocos.cocos.diseases;

import com.cocos.cocos.api.disease.dto.response.DiseaseResponse;
import com.cocos.cocos.api.disease.dto.response.DiseasesOfBodiesResponse;
import com.cocos.cocos.api.disease.dto.response.DiseasesOfBodyResponse;
import com.cocos.cocos.api.disease.service.DiseaseService;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
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
@DisplayName("질병 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DiseaseServiceTest {

    @InjectMocks
    private DiseaseService diseaseService;

    @Mock
    private DiseaseRepository diseaseRepository;

    @Mock
    private BodyRepository bodyRepository;

    @Test
    @DisplayName("신체 부위 아이디에 해당하는 질병 리스트를 조회할 수 있다.")
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

        final Disease disease1 = Disease.builder()
                .name("질병1")
                .bodyId(1L)
                .build();
        final Disease disease2 = Disease.builder()
                .name("질병2")
                .bodyId(2L)
                .build();
        final List<Disease> diseases1 = new ArrayList<>(List.of(disease1));
        final List<Disease> diseases2 = new ArrayList<>(List.of(disease2));

        BDDMockito.given(bodyRepository.findById(1L)).willReturn(Optional.ofNullable(body1));
        BDDMockito.given(bodyRepository.findById(2L)).willReturn(Optional.ofNullable(body2));
        BDDMockito.given(diseaseRepository.findAllByBodyId(1L)).willReturn(diseases1);
        BDDMockito.given(diseaseRepository.findAllByBodyId(2L)).willReturn(diseases2);

        final DiseasesOfBodiesResponse expected = DiseasesOfBodiesResponse.of(
                bodyIds.stream()
                        .map(bodyId -> {
                            if (bodyId == 1L) {
                                return DiseasesOfBodyResponse.of(bodyId, body1.getName(),
                                        diseases1.stream()
                                                .map(disease -> DiseaseResponse.of(disease.getId(), disease.getName()))
                                                .toList()
                                );
                            } else {
                                return DiseasesOfBodyResponse.of(bodyId, body2.getName(),
                                        diseases2.stream()
                                                .map(disease -> DiseaseResponse.of(disease.getId(), disease.getName()))
                                                .toList()
                                );
                            }
                        })
                        .toList()
        );

        //when
        final DiseasesOfBodiesResponse actual = diseaseService.getDiseases(bodyIds);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("bodyIds가 null일 때 빈 배열을 반환한다. ")
    void returnEmptyListWhenBodyIdsIsNull() {
        //given

        final List<Long> bodyIds = null;

        final DiseasesOfBodiesResponse expected = DiseasesOfBodiesResponse.of(
                List.of()
        );

        //when
        final DiseasesOfBodiesResponse actual = diseaseService.getDiseases(bodyIds);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
