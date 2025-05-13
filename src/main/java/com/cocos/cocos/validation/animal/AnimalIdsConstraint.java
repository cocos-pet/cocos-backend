package com.cocos.cocos.validation.animal;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AnimalIdsValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnimalIdsConstraint {
    String message() default "Invalid Animal Ids";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
