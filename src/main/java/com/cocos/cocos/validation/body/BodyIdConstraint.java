package com.cocos.cocos.validation.body;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BodyIdValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BodyIdConstraint {
    String message() default "Invalid Body Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
