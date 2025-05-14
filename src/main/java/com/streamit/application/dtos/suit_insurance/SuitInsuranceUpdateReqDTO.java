package com.streamit.application.dtos.suit_insurance;

import com.streamit.application.annotations.common.NotEmptyFile;
import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.annotations.common.ValidStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SuitInsuranceUpdateReqDTO {

    //    common content
    @Length(min = 1, max = 64, message = "title length must be between 1 and 64")
    private String title;

    @FutureOrPresent(message = "effectiveFrom must be in the present or future")
    private LocalDateTime effectiveFrom;

    @Future(message = "effectiveTo must be in the future")
    private LocalDateTime effectiveTo;

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
