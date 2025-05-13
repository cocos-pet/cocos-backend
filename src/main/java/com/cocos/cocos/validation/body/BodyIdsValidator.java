package com.cocos.cocos.validation.body;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.body.repository.BodyRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BodyIdsValidator implements ConstraintValidator<BodyIdsConstraint, List<Long>>  {

    private final BodyRepository bodyRepository;

    @Override
    public boolean isValid(final List<Long> bodyIds, final ConstraintValidatorContext constraintValidatorContext) {
        final long validCount = bodyRepository.countByIdIn(bodyIds);
        if( validCount != bodyIds.size() ) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_BODY_ID);
        }
        return true;
    }
}
