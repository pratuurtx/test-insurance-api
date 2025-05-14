package com.streamit.application.validator.common;

import com.streamit.application.annotations.common.ValidItemQueryContent;
import com.streamit.application.dtos.common.ItemQueryParamsReqDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemQueryContentValidator implements ConstraintValidator<ValidItemQueryContent, ItemQueryParamsReqDTO> {

    @Override
    public boolean isValid(ItemQueryParamsReqDTO itemQueryParamsReqDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (itemQueryParamsReqDTO == null) {
            return true;
        }

        itemQueryParamsReqDTO.applyDefault(itemQueryParamsReqDTO);

        return true;
    }
}
