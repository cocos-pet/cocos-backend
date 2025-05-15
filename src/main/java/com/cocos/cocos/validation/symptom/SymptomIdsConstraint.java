package com.cocos.cocos.validation.symptom;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SymptomIdsValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SymptomIdsConstraint {
    String message() default "Invalid Symptom Ids";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
