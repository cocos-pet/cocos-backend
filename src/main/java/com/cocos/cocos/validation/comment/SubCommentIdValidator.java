package com.cocos.cocos.validation.comment;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.comment.repository.SubCommentRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubCommentIdValidator implements ConstraintValidator<SubCommentIdConstraint, Long> {

    private final SubCommentRepository subCommentRepository;

    @Override
    public boolean isValid(final Long subCommentId, ConstraintValidatorContext constraintValidatorContext) {
        if (!subCommentRepository.existsById(subCommentId)) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_SUB_COMMENT_ID);
        }
        return true;
    }
}
