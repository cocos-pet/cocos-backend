package com.cocos.cocos.validation.review;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.review.repository.ReviewRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewIdValidator implements ConstraintValidator<ReviewIdConstraint, Long> {

    private final ReviewRepository reviewRepository;

    @Override
    public boolean isValid(Long reviewId, ConstraintValidatorContext constraintValidatorContext) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_REVIEW_ID);
        }
        return true;
    }
}
