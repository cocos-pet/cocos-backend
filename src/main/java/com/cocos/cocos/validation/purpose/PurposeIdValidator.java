package com.cocos.cocos.validation.purpose;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.hospital.repository.HospitalVisitPurposeRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurposeIdValidator implements ConstraintValidator<PurposeIdConstraint, Long> {

    private final HospitalVisitPurposeRepository hospitalVisitPurposeRepository;

    @Override
    public boolean isValid(final Long purposeId, final ConstraintValidatorContext constraintValidatorContext) {
        if (purposeId == null || hospitalVisitPurposeRepository.existsById(purposeId)) {
            return true;
        }
        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_PURPOSE_ID);
    }
}
