package com.cocos.cocos.api.location.service;

import com.cocos.cocos.api.location.dto.response.CityResponse;
import com.cocos.cocos.api.location.dto.response.DistrictResponse;
import com.cocos.cocos.api.location.dto.response.LocationResponse;
import com.cocos.cocos.db.city.entity.City;
import com.cocos.cocos.db.city.repository.CityRepository;
import com.cocos.cocos.db.district.entity.District;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;

    @Transactional(readOnly = true)
    public LocationResponse getLocations() {
        final List<City> cities = cityRepository.findAll();
        return LocationResponse.of(
                cities.stream()
                        .map(city -> {
                            final List<District> districts = districtRepository.findByCityId(city.getId());
                            return CityResponse.of(
                                    city.getId(),
                                    city.getName(),
                                    districts.stream()
                                            .map(district -> DistrictResponse.of(district.getId(), district.getName()))
                                            .toList());

                        })
                        .toList()
        );
    }
}
