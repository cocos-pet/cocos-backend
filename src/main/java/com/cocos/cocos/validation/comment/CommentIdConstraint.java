package com.cocos.cocos.validation.comment;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CommentIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommentIdConstraint {
    String message() default "Invalid Comment Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
