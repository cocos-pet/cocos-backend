package com.cocos.cocos.validation.animal;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnimalIdValidator implements ConstraintValidator<AnimalIdConstraint, Long> {

    private final AnimalRepository animalRepository;

    @Override
    public boolean isValid(final Long animalId, final ConstraintValidatorContext constraintValidatorContext) {
        if (animalId == null || animalRepository.existsById(animalId)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_ANIMAL_ID);
        }
    }
}
