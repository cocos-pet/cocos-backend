package com.cocos.cocos.api.location.service;

import com.cocos.cocos.api.location.dto.response.CityResponse;
import com.cocos.cocos.api.location.dto.response.DistrictResponse;
import com.cocos.cocos.api.location.dto.response.LocationResponse;
import com.cocos.cocos.db.city.entity.City;
import com.cocos.cocos.db.city.repository.CityRepository;
import com.cocos.cocos.db.district.entity.District;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import com.cocos.cocos.enums.location.LocationType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;

    private static final String ALL_CITY_NAME_SUFFIX = " 전체";

    @Transactional(readOnly = true)
    public LocationResponse getLocations() {
        final List<City> cities = cityRepository.findAll();
        return LocationResponse.of(
                cities.stream()
                        .map(city -> {
                            final List<District> districts = districtRepository.findAllByCityId(city.getId());
                            final List<DistrictResponse> districtResponses = new ArrayList<>(List.of(
                                    DistrictResponse.of(city.getId(), city.getName() + ALL_CITY_NAME_SUFFIX,
                                            LocationType.CITY.name())
                            ));
                            districtResponses.addAll(
                                    districts.stream()
                                            .map(district -> DistrictResponse.of(district.getId(), district.getName(),
                                                    LocationType.DISTRICT.name()))
                                            .toList()
                            );

                            return CityResponse.of(
                                    city.getId(),
                                    city.getName(),
                                    districtResponses
                            );

                        })
                        .toList()
        );
    }
}
