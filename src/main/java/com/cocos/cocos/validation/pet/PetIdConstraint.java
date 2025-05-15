package com.cocos.cocos.validation.pet;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PetIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PetIdConstraint {
    String message() default "Invalid Pet Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
