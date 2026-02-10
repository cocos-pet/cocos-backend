package com.cocos.cocos.validation.pet;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AgeOrBirthDateRequiredValidator.class)
public @interface AgeOrBirthDateRequiredConstraint {

    String message() default "age 또는 birthDate 중 하나는 반드시 입력되어야 합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
