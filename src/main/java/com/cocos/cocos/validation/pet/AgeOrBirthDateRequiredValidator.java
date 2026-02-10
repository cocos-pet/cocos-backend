package com.cocos.cocos.validation.pet;

import com.cocos.cocos.api.pet.dto.request.PetCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AgeOrBirthDateRequiredValidator
        implements ConstraintValidator<AgeOrBirthDateRequiredConstraint, PetCreateRequest> {

    @Override
    public boolean isValid(
            PetCreateRequest value,
            ConstraintValidatorContext context
    ) {
        if (value == null) {
            return true;
        }
        return value.age() != null || value.birthDate() != null;
    }
}
