package com.cocos.cocos.body;

import com.cocos.cocos.api.body.dto.response.BodiesResponse;
import com.cocos.cocos.api.body.dto.response.BodyResponse;
import com.cocos.cocos.api.body.service.BodyService;
import com.cocos.cocos.db.body.entity.Body;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.db.disease.entity.Disease;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.enums.pet.PetProblem;
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
@DisplayName("신체 부위 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BodyServiceTest {

    @InjectMocks
    private BodyService bodyService;

    @Mock
    private BodyRepository bodyRepository;

    @Mock
    private DiseaseRepository diseaseRepository;

    @Mock
    private CloudfrontClient cloudfrontClient;

    @Test
    @DisplayName("신체 부위 리스트를 조회할 수 있다.")
    void getBodies() {
        //given
        final Body body1 = Body.builder()
                .name("이름1")
                .image("이미지1")
                .build();
        final Body body2 = Body.builder()
                .name("이름2")
                .image("이미지2")
                .build();
        final Disease disease1 = Disease.builder()
                .bodyId(1L)
                .name("질병1")
                .build();
        final Disease disease2 = Disease.builder()
                .bodyId(2L)
                .name("질병2")
                .build();
        final List<Disease> diseases = new ArrayList<>(List.of(disease1, disease2));
        final List<Body> bodies = new ArrayList<Body>(List.of(body1, body2));
        final BodiesResponse expected = BodiesResponse.of(
                bodies.stream()
                        .map(body -> BodyResponse.of(body.getId(), body.getName(), "image"))
                        .toList()
        );

        BDDMockito.given(diseaseRepository.findAll()).willReturn(diseases);
        BDDMockito.given(bodyRepository.findAllById(any())).willReturn(bodies);
        BDDMockito.given(cloudfrontClient.getAppCloudfrontUrl(any())).willReturn("image");

        //when
        final BodiesResponse actual = bodyService.getBodies(PetProblem.DISEASE);

        //then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
