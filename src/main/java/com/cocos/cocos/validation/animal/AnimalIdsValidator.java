package com.cocos.cocos.validation.animal;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.animal.repository.AnimalRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnimalIdsValidator implements ConstraintValidator<AnimalIdsConstraint, List<Long>> {

    private final AnimalRepository animalRepository;

    @Override
    public boolean isValid(final List<Long> animalIds, final ConstraintValidatorContext constraintValidatorContext) {
        final long validCount = animalRepository.countByIdIn(animalIds);
        if (animalIds == null || validCount == animalIds.size()) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_ANIMAL_ID);
        }
    }

}
