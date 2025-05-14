package com.streamit.application.annotations.common;

import com.streamit.application.validator.common.ImageFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageFileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "Invalid image file (Allowed: JPEG, PNG, GIF)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedExtensions() default {"jpg", "jpeg", "png", "gif", "webp", "avif", "jfif"};
}