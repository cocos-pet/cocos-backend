package com.cocos.cocos.validation.body;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BodyIdValidator implements ConstraintValidator<BodyIdConstraint, Long> {

    private final BodyRepository bodyRepository;

    @Override
    public boolean isValid(final Long bodyId, final ConstraintValidatorContext constraintValidatorContext) {
        if (bodyId == null || bodyRepository.existsById(bodyId)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_BODY_ID);
        }
    }
}
