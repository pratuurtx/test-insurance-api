package com.streamit.application.validator.common;

import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.utils.FileUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private String[] allowedExtensions;

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.allowedExtensions = constraintAnnotation.allowedExtensions();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        var fileDetail = FileUtil.getFileDetail(file.getOriginalFilename());
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equalsIgnoreCase(fileDetail.getExtension())) {
                return true;
            }
        }
        return false;
    }
}
