package com.cocos.cocos.validation.review;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SummaryOptionIdValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SummaryOptionIdConstraint {
    String message() default "Invalid Summary Option Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
