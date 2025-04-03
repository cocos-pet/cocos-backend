package com.cocos.cocos.location;

import com.cocos.cocos.api.location.dto.response.CityResponse;
import com.cocos.cocos.api.location.dto.response.DistrictResponse;
import com.cocos.cocos.api.location.dto.response.LocationResponse;
import com.cocos.cocos.api.location.service.LocationService;
import com.cocos.cocos.db.city.entity.City;
import com.cocos.cocos.db.city.repository.CityRepository;
import com.cocos.cocos.db.district.entity.District;
import com.cocos.cocos.db.district.repository.DistrictRepository;
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
@DisplayName("지역 서비스 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class LocationServiceTest {

    @InjectMocks
    LocationService locationService;
    @Mock
    CityRepository cityRepository;
    @Mock
    DistrictRepository districtRepository;


    @Test
    @DisplayName("지역을 모두 조회할 수 있다.")
    void addPostComment() {
        // given
        final Long cityId = 1L;
        final City city = City.builder()
                .name("시/도")
                .build();

        final District district1 = District.builder()
                .name("시/군/구1")
                .cityId(cityId)
                .build();

        final District district2 = District.builder()
                .name("시/군/구2")
                .cityId(cityId)
                .build();

        final List<City> cities = new ArrayList<>(List.of(city));
        final List<District> districts = new ArrayList<>(List.of(district1, district2));

        BDDMockito.given(cityRepository.findAll()).willReturn(cities);
        BDDMockito.given(districtRepository.findByCityId(any())).willReturn(districts);

        final DistrictResponse districtResponse1 = DistrictResponse.of(district1.getId(), district1.getName());
        final DistrictResponse districtResponse2 = DistrictResponse.of(district2.getId(), district2.getName());
        final CityResponse cityResponse = CityResponse.of(city.getId(), city.getName(), List.of(districtResponse1, districtResponse2));
        final LocationResponse expected = LocationResponse.of(List.of(cityResponse));

        // when
        final LocationResponse actual = locationService.getLocations();

        // then
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        Assertions.assertThat(actual.cities().getFirst().name()).isEqualTo("시/도");
    }
}
