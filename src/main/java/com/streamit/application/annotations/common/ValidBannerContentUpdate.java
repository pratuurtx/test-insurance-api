package com.streamit.application.annotations.common;

import com.streamit.application.validator.common.BannerContentUpdateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BannerContentUpdateValidator.class)
@Documented
public @interface ValidBannerContentUpdate {
    String message() default "id is required when contentImage or hyperLink is present";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
