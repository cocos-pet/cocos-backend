package com.cocos.cocos.validation.symptom;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.symptom.repository.SymptomRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SymptomIdsValidator implements ConstraintValidator<SymptomIdsConstraint, List<Long>> {

    private final SymptomRepository symptomRepository;

    @Override
    public boolean isValid(final List<Long> symptomIds, ConstraintValidatorContext constraintValidatorContext) {
        final long validCount = symptomRepository.countByIdIn(symptomIds);
        if (symptomIds == null || validCount == symptomIds.size()) {
            return true;
        }
        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_SYMPTOM_ID);
    }
}
