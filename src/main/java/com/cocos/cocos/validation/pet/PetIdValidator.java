package com.cocos.cocos.validation.pet;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.pet.repository.PetRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetIdValidator implements ConstraintValidator<PetIdConstraint, Long> {

    private final PetRepository petRepository;

    @Override
    public boolean isValid(final Long petId, final ConstraintValidatorContext constraintValidatorContext) {
        if (petId == null || petRepository.existsById(petId)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_PET_ID);
        }
    }
}
