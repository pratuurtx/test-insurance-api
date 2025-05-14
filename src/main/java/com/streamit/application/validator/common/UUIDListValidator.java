package com.streamit.application.validator.common;

import com.streamit.application.annotations.common.ValidUUIDList;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public class UUIDListValidator implements ConstraintValidator<ValidUUIDList, List<String>> {
    @Override
    public boolean isValid(List<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        if (strings == null || strings.isEmpty()) {
            return true;
        }


        for (String str : strings) {
            try {
                UUID.fromString(str);
            } catch (IllegalArgumentException ex) {
                log.error(str, ex);
                return false;
            }
        }

        return true;
    }
}
