package com.cocos.cocos.validation.review;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.review.repository.ReviewSummaryOptionRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoodReviewIdsValidator implements ConstraintValidator<GoodReviewIdsConstraint, List<Long>> {

    private final ReviewSummaryOptionRepository reviewSummaryOptionRepository;

    @Override
    public boolean isValid(final List<Long> summaryOptionIds, final ConstraintValidatorContext constraintValidatorContext) {
        final long validCount = reviewSummaryOptionRepository.countByIdInAndIsGoodTrue(summaryOptionIds);
        if (summaryOptionIds == null || validCount == summaryOptionIds.size()) {
            return true;
        }
        throw new CocosException(FailMessage.BAD_REQUEST_INVALID_GOOD_SUMMARY_OPTION_ID);
    }
}
