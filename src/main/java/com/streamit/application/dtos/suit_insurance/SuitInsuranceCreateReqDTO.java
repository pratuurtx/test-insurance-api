package com.streamit.application.dtos.suit_insurance;

import com.streamit.application.annotations.common.ValidImage;
import com.streamit.application.annotations.common.ValidStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SuitInsuranceCreateReqDTO {
    //    common content
    @NotBlank(message = "title is required")
    @Length(min = 1, max = 64, message = "title length must be between 1 and 64")
    private String title;

    @NotNull(message = "effectiveFrom is required")
    @FutureOrPresent(message = "effectiveFrom must be in the present or future")
    private LocalDateTime effectiveFrom;

    @NotNull(message = "effectiveTo is required")
    @Future(message = "effectiveTo must be in the future")
    private LocalDateTime effectiveTo;

    @NotNull(message = "status is required")
    @ValidStatus
    private String status;
    //

    @NotBlank(message = "titleTh is required")
    @Length(min = 1, max = 63, message = "titleTh length must be between 1 and 63")
    private String titleTh;

    @NotBlank(message = "titleEn is required")
    @Length(min = 1, max = 63, message = "titleEn length must be between 1 and 63")
    private String titleEn;

    @NotNull(message = "image is required")
    @ValidImage(message = "image invalid file extension")
    private MultipartFile image;
}
