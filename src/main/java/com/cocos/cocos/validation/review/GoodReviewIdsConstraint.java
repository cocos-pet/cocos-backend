package com.cocos.cocos.validation.review;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = GoodReviewIdsValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GoodReviewIdsConstraint {
    String message() default "Invalid Good Review Ids";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
