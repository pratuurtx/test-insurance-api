package com.streamit.application.dtos.promotion;

import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.annotations.common.ValidStatus;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PromotionUpdateReqDTO {
    // Common content fields
    @Length(min = 1, max = 64, message = "title length must be between 1 and 64")
    private String title;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "effectiveFrom must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String effectiveFrom;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "effectiveTo must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String effectiveTo;

    @ValidStatus
    private String status;

    // Promotion-specific fields
    @Length(min = 1, max = 31, message = "titleTh length must be between 1 and 31")
    private String titleTh;

    @Length(min = 1, max = 31, message = "titleEn length must be between 1 and 31")
    private String titleEn;

    @Length(min = 1, max = 255, message = "descriptionTh length must be between 1 and 255")
    private String descriptionTh;

    @Length(min = 1, max = 255, message = "descriptionEn length must be between 1 and 255")
    private String descriptionEn;

    @ValidImage(message = "cover image invalid file extension")
    private MultipartFile coverImage;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "startDate must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String startDate;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "endDate must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String endDate;

    @Override
    public String toString() {
        return "PromotionUpdateReqDTO{" +
                "title='" + title + '\'' +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", status='" + status + '\'' +
                ", titleTh='" + titleTh + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", descriptionTh='" + descriptionTh + '\'' +
                ", descriptionEn='" + descriptionEn + '\'' +
                ", coverImage=" + (coverImage == null ? "null" : coverImage.getOriginalFilename()) +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}