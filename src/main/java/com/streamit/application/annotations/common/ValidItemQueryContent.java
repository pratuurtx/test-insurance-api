package com.streamit.application.annotations.common;

import com.streamit.application.validator.common.ItemQueryContentValidator;
import com.streamit.application.validator.common.StatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ItemQueryContentValidator.class)
public @interface ValidItemQueryContent {
    String message() default "Invalid status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

