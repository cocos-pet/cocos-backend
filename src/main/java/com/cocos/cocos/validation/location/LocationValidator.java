package com.cocos.cocos.validation.location;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.city.repository.CityRepository;
import com.cocos.cocos.db.district.repository.DistrictRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.cocos.cocos.enums.location.LocationType.CITY;
import static com.cocos.cocos.enums.location.LocationType.DISTRICT;

@Component
@RequiredArgsConstructor
public class LocationValidator implements ConstraintValidator<LocationConstraint, HasLocation> {

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;

    @Override
    public boolean isValid(HasLocation request, ConstraintValidatorContext constraintValidatorContext) {
        if (request.locationId() == null || request.locationType() == null) {
            return true;
        }

        if (request.locationType() == CITY && !cityRepository.existsById(request.locationId())) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_CITY_ID);
        }
        if (request.locationType() == DISTRICT && !districtRepository.existsById(request.locationId())) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_DISTRICT_ID);
        }
        return true;
    }
}
