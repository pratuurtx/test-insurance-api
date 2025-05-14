package com.streamit.application.dtos.suit_insurance;

import com.streamit.application.annotations.common.NotEmptyFile;
import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.annotations.common.ValidStatus;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class SuitInsuranceUpdateReqDTO {

    //    common content
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
    //

    @Length(min = 1, max = 63, message = "titleTh length must be between 1 and 63")
    private String titleTh;

    @Length(min = 1, max = 63, message = "titleEn length must be between 1 and 63")
    private String titleEn;

    @NotEmptyFile(message = "image is not empty")
    @ValidImage(message = "image invalid file extension")
    private MultipartFile image;

    @Override
    public String toString() {
        return "SuitInsuranceUpdateReqDTO{" +
                "title='" + title + '\'' +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", status='" + status + '\'' +
                ", titleTh='" + titleTh + '\'' +
                ", titleEn='" + titleEn + '\'' +
                ", image=" + (image == null ? "null" : image.getOriginalFilename()) +
                '}';
    }
}
