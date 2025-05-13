package com.cocos.cocos.validation.disease;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiseaseIdsValidator implements ConstraintValidator<DiseaseIdsConstraint, List<Long>> {

    private final DiseaseRepository diseaseRepository;

    @Override
    public boolean isValid(List<Long> diseaseIds, ConstraintValidatorContext constraintValidatorContext) {
        final long validCount = diseaseRepository.countByIdIn(diseaseIds);
        if (diseaseIds == null || validCount == diseaseIds.size()) {
            return true;
        }
        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_DISEASE_ID);
    }
}
