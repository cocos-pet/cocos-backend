package com.cocos.cocos.validation.post;

import com.cocos.cocos.common.exception.CocosException;
import com.cocos.cocos.db.post.repository.PostRepository;
import com.cocos.cocos.enums.message.FailMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostIdValidator implements ConstraintValidator<PostIdConstraint, Long> {

    private final PostRepository postRepository;

    @Override
    public boolean isValid(final Long postId, final ConstraintValidatorContext constraintValidatorContext) {
        if (!postRepository.existsById(postId)) {
            throw new CocosException(FailMessage.BAD_REQUEST_INVALID_POST_ID);
        }
        return true;
    }
}
