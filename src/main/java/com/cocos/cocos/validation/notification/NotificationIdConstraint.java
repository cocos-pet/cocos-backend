package com.cocos.cocos.validation.notification;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotificationIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotificationIdConstraint {
    String message() default "Invalid Notification";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
