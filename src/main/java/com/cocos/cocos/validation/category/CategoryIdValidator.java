package com.cocos.cocos.validation.category;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.post.repository.PostCategoryRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryIdValidator implements ConstraintValidator<CategoryIdConstraint, Long> {

    private final PostCategoryRepository postCategoryRepository;

    @Override
    public boolean isValid(final Long categoryId, final ConstraintValidatorContext constraintValidatorContext) {
        if (categoryId == null || postCategoryRepository.existsById(categoryId)) {
            return true;
        } else {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_CATEGORY_ID);
        }
    }
}
