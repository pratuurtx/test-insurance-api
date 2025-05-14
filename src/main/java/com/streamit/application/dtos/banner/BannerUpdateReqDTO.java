package com.streamit.application.dtos.banner;

import com.streamit.application.annotations.common.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BannerUpdateReqDTO {
    @Length(min = 1, max = 63, message = "title length must be between 1 and 63")
    private String title;

    @FutureOrPresent(message = "effectiveFrom must be in the present or future")
    private LocalDateTime effectiveFrom;

    @Future(message = "effectiveTo must be in the future")
    private LocalDateTime effectiveTo;

    @ValidStatus
    private String status;

    @NotEmptyFile(message = "coverImage is not empty")
    @ValidImage(message = "coverImage is invalid allowed image file (JPEG, PNG, GIF)")
    private MultipartFile coverImage;

    @URL(message = "coverHyperLink must be a valid URL")
    private String coverHyperLink;

    @Valid
    private List<BannerContentUpdateReqDTO> contentUpdates;

    @ValidBannerContent
    private List<BannerContentCreateReqDTO> contentCreates;

    @ValidUUIDList
    private List<String> contentRemoves;

    @Override
    public String toString() {
        return "BannerUpdateReqDTO{" +
                "title='" + title + '\'' +
                ", effectiveFrom=" + effectiveFrom +
                ", effectiveTo=" + effectiveTo +
                ", status='" + status + '\'' +
                ", coverImage=" + coverImage +
                ", coverHyperLink='" + coverHyperLink + '\'' +
                ", contentUpdates=" + contentUpdates +
                ", contentCreates=" + contentCreates +
                ", contentRemoves=" + contentRemoves +
                '}';
    }
}
