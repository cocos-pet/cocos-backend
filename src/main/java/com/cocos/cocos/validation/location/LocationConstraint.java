package com.cocos.cocos.validation.location;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocationValidator.class)
public @interface LocationConstraint {
    String message() default "Invalid Location";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
//
