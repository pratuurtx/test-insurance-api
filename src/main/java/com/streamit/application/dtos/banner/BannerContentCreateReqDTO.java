package com.streamit.application.dtos.banner;

import com.streamit.application.annotations.common.NotEmptyFile;
import com.streamit.application.annotations.common.ValidImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BannerContentCreateReqDTO {
    @NotNull(message = "contentImage is required")
    @NotEmptyFile(message = "contentImage must not be empty")
    @ValidImage(message = "contentImage must be JPEG/PNG/GIF")
    private MultipartFile contentImage;

    @NotBlank(message = "contentHyperLink is required")
    @URL(message = "contentHyperLink must be a valid URL")
    private String contentHyperLink;

    @Override
    public String toString() {
        return "BannerContentCreateReqDTO{" +
                "contentImage=" + (contentImage != null ?
                "[file: " + contentImage.getOriginalFilename() + ", size: " + contentImage.getSize() + "]" : "null") +
                ", contentHyperLink='" + contentHyperLink + '\'' +
                '}';
    }
}
