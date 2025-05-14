package com.streamit.application.annotations.common;

import com.streamit.application.validator.common.BannerContentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BannerContentValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBannerContent {
    String message() default "Invalid list contents";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedImageExtensions() default {"jpg", "jpeg", "png", "gif", "webp", "svg"};
}
