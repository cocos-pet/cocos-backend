package com.cocos.cocos.validation.hospital;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = HospitalIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HospitalIdConstraint {
    String message() default "Invalid Hospital Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
