package com.cocos.cocos.validation.post;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PostIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PostIdConstraint {
    String message() default "Invalid Post Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
