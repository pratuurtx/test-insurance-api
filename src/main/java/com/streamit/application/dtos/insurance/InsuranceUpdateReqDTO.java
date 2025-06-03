package com.streamit.application.dtos.insurance;

import com.streamit.application.annotations.common.NotEmptyFile;
import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.annotations.common.ValidStatus;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class InsuranceUpdateReqDTO {
    //    common content
    @Length(min = 1, max = 255, message = "title length must be between 1 and 255")
    private String title;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "effectiveFrom must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String effectiveFrom;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "effectiveTo must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String effectiveTo;

    @ValidStatus
    private String status;
    //

    @Length(min = 1, max = 255, message = "titleTh length must be between 1 and 255")
    private String titleTh;

    @Length(min = 1, max = 255, message = "titleEn length must be between 1 and 255")
    private String titleEn;

    @Length(min = 1, max = 255, message = "descriptionTh length must be between 1 and 255")
    private String descriptionTh;

    @Length(min = 1, max = 255, message = "descriptionEn length must be between 1 and 255")
    private String descriptionEn;

    @NotEmptyFile(message = "coverImage is not empty")
    @ValidImage(message = "coverImage invalid file extension")
    private MultipartFile coverImage;

    @NotEmptyFile(message = "iconImage is not empty")
    @ValidImage(message = "iconImage invalid file extension")
    private MultipartFile iconImage;

    @Override
    public String toString() {
        return "InsuranceUpdateReqDTO{" +
                "title='" + title + '\'' +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", status='" + status + '\'' +
                ", titleTh='" + titleTh + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", descriptionTh='" + descriptionTh + '\'' +
                ", descriptionEn='" + descriptionEn + '\'' +
                ", coverImage=" + (coverImage == null ? "null" : coverImage.getOriginalFilename()) +
                ", iconImage=" + (iconImage == null ? "null" : iconImage.getOriginalFilename()) +
                '}';
    }
}
