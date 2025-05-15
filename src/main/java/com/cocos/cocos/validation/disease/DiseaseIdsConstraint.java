package com.cocos.cocos.validation.disease;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DiseaseIdsValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DiseaseIdsConstraint {
    String message() default "Invalid Disease Ids";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
