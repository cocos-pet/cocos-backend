package com.cocos.cocos.validation.breed;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.breed.repository.BreedRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BreedIdValidator implements ConstraintValidator<BreedIdConstraint, Long> {

    private final BreedRepository breedRepository;

    @Override
    public boolean isValid(Long breedId, ConstraintValidatorContext constraintValidatorContext) {
        if (!breedRepository.existsById(breedId)) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_BREED_ID);
        }
        return true;
    }
}
