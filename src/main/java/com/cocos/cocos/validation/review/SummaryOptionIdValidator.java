package com.cocos.cocos.validation.review;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.review.repository.ReviewSummaryOptionRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SummaryOptionIdValidator implements ConstraintValidator<SummaryOptionIdConstraint, Long> {

    private final ReviewSummaryOptionRepository reviewSummaryOptionRepository;

    @Override
    public boolean isValid(final Long summaryOptionId, final ConstraintValidatorContext constraintValidatorContext) {
        if (summaryOptionId == null || reviewSummaryOptionRepository.existsById(summaryOptionId)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_SUMMARY_OPTION_ID);
        }
    }
}
