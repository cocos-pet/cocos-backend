package com.cocos.cocos.validation.review;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ReviewIdValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReviewIdConstraint {
    String message() default "Invalid Review Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
