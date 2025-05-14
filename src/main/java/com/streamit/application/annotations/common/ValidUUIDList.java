package com.streamit.application.annotations.common;

import com.streamit.application.validator.common.UUIDListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UUIDListValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUIDList {
    String message() default "Invalid UUID format in list";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
