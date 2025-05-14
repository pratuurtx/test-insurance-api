package com.streamit.application.validator.common;

import com.streamit.application.annotations.common.ValidBannerContent;
import com.streamit.application.dtos.banner.BannerContentCreateReqDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BannerContentValidator implements ConstraintValidator<ValidBannerContent, List<BannerContentCreateReqDTO>> {

    private Set<String> allowedImageExtensions;

    @Override
    public void initialize(ValidBannerContent constraintAnnotation) {
        this.allowedImageExtensions = Stream.of(constraintAnnotation.allowedImageExtensions())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(List<BannerContentCreateReqDTO> bannerContents, ConstraintValidatorContext context) {
        if (bannerContents == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        for (int idx = 0; idx < bannerContents.size(); idx++) {
            BannerContentCreateReqDTO banner = bannerContents.get(idx);

            if (banner.getContentImage() == null || banner.getContentImage().isEmpty()) {
                addConstraintViolation(context, "contentImage[" + idx + "] cannot be empty", idx);
                isValid = false;
            } else if (!isValidImageFile(banner.getContentImage().getOriginalFilename())) {
                addConstraintViolation(context, "contentImage[" + idx + "] must be a valid image", idx);
                isValid = false;
            }

            if (banner.getContentHyperLink() == null || banner.getContentHyperLink().isEmpty()) {
                addConstraintViolation(context, "contentHyperlink[" + idx + "] cannot be empty", idx);
                isValid = false;
            } else if (!isValidUrl(banner.getContentHyperLink())) {
                addConstraintViolation(context, "contentHyperlink[" + idx + "] must be a valid URL", idx);
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean isValidImageFile(String imagePath) {
        if (imagePath == null) return false;

        String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1).toLowerCase();
        return allowedImageExtensions.contains(extension);
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message, int index) {
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("contents")
                .addPropertyNode(String.valueOf(index))
                .addConstraintViolation();
    }
}