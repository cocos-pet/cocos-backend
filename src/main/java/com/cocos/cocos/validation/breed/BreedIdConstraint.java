package com.cocos.cocos.validation.breed;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BreedIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BreedIdConstraint {
    String message() default "Invalid Breed Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
