package com.cocos.cocos.validation.review;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BadReviewIdsValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BadReviewIdsConstraint {
    String message() default "Invalid Bad Review Ids";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
