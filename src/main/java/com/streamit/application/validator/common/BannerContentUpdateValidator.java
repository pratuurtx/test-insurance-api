package com.streamit.application.validator.common;

import com.streamit.application.annotations.common.ValidBannerContentUpdate;
import com.streamit.application.dtos.banner.BannerContentUpdateReqDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BannerContentUpdateValidator implements ConstraintValidator<ValidBannerContentUpdate, BannerContentUpdateReqDTO> {

    @Override
    public boolean isValid(BannerContentUpdateReqDTO dto, ConstraintValidatorContext context) {
        boolean hasContent = (dto.getContentImage() != null && !dto.getContentImage().isEmpty())
                || (dto.getContentHyperLink() != null && !dto.getContentHyperLink().isEmpty());

        if (hasContent) {
            return dto.getId() != null && !dto.getId().isEmpty();
        }

        return true; // if nothing to update, it's okay
    }
}