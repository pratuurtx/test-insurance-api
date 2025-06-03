package com.streamit.application.dtos.banner;

import com.streamit.application.annotations.common.NotEmptyFile;
import com.streamit.application.annotations.common.ValidBannerContent;
import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.annotations.common.ValidStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BannerCreateReqDTO {
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

    @NotNull(message = "coverImage is required")
    @NotEmptyFile(message = "coverImage is not empty")
    @ValidImage(message = "coverImage is invalid allowed image file (JPEG, PNG, GIF)")
    private MultipartFile coverImage;

    @NotBlank(message = "coverHyperLink is required")
    @URL(message = "coverHyperLink must be a valid URL")
    private String coverHyperLink;

    @Size(max = 10, message = "Maximum 10 items allowed in contents")
    @ValidBannerContent
    private List<BannerContentCreateReqDTO> contents;

    @Override
    public String toString() {
        return "BannerCreateReqDTO{" +
                "coverImage=" + (coverImage != null ?
                "[file: " + coverImage.getOriginalFilename() + ", size: " + coverImage.getSize() + "]" : "null") +
                ", coverHyperLink='" + coverHyperLink + '\'' +
                ", contents=" + contents +
                '}';
    }
}
