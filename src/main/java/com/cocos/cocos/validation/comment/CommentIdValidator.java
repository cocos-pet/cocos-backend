package com.cocos.cocos.validation.comment;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.comment.repository.CommentRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentIdValidator implements ConstraintValidator<CommentIdConstraint, Long> {

    private final CommentRepository commentRepository;

    @Override
    public boolean isValid(final Long commentId, ConstraintValidatorContext constraintValidatorContext) {
        if (!commentRepository.existsById(commentId)) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_COMMENT_ID);
        }
        return true;
    }
}
