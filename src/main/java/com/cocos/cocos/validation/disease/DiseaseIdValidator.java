package com.cocos.cocos.validation.disease;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.disease.repository.DiseaseRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiseaseIdValidator implements ConstraintValidator<DiseaseIdConstraint, Long> {

    private final DiseaseRepository diseaseRepository;

    @Override
    public boolean isValid(Long diseaseId, ConstraintValidatorContext constraintValidatorContext) {
        if (diseaseId == null || diseaseRepository.existsById(diseaseId)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_DISEASE_ID);
        }
    }
}
