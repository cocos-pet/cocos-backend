package com.cocos.cocos.validation.hospital;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.hospital.repository.HospitalRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HospitalIdValidator implements ConstraintValidator<HospitalIdConstraint, Long> {

    private final HospitalRepository hospitalRepository;

    @Override
    public boolean isValid(Long hospitalId, ConstraintValidatorContext constraintValidatorContext) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_HOSPITAL_ID);
        }
        return true;
    }
}
