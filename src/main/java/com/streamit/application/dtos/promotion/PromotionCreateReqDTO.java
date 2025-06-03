package com.streamit.application.dtos.promotion;

import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.annotations.common.ValidStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionCreateReqDTO {
    // Common content fields
    @NotBlank(message = "title is required")
    @Length(min = 1, max = 255, message = "title length must be between 1 and 255")
    private String title;

    @NotNull(message = "effectiveFrom is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "effectiveFrom must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String effectiveFrom;

    @NotNull(message = "effectiveTo is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "effectiveTo must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String effectiveTo;

    @NotNull(message = "status is required")
    @ValidStatus
    private String status;

    @NotBlank(message = "titleTh is required")
    @Length(min = 1, max = 255, message = "titleTh length must be between 1 and 255")
    private String titleTh;

    @NotBlank(message = "titleEn is required")
    @Length(min = 1, max = 255, message = "titleEn length must be between 1 and 255")
    private String titleEn;

    @NotBlank(message = "descriptionTh is required")
    private String descriptionTh;

    @NotBlank(message = "descriptionEn is required")
    private String descriptionEn;

    @NotNull(message = "cover image is required")
    @ValidImage(message = "cover image invalid file extension")
    private MultipartFile coverImage;

    @NotNull(message = "startDate is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "startDate must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String startDate;

    @NotNull(message = "endDate is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
            message = "endDate must be in ISO-8601 format (yyyy-MM-ddTHH:mm:ss)")
    private String endDate;
}