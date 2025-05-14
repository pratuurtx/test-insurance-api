package com.streamit.application.dtos.banner;

import com.streamit.application.annotations.common.NotEmptyFile;
import com.streamit.application.annotations.common.ValidBannerContentUpdate;
import com.streamit.application.annotations.common.ValidImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;
import org.springframework.web.multipart.MultipartFile;

@ValidBannerContentUpdate
@Getter
@Setter
public class BannerContentUpdateReqDTO {
    @UUID(message = "invalid uuid")
    private String id;

    @NotEmptyFile(message = "contentImage must not be empty")
    @ValidImage(message = "contentImage must be JPEG/PNG/GIF")
    private MultipartFile contentImage;

    @URL(message = "contentHyperLink must be a valid URL")
    private String contentHyperLink;

    @Override
    public String toString() {
        return "BannerContentUpdateReqDTO{" +
                "id='" + id + '\'' +
                "contentImage=" + (contentImage != null ? contentImage.getOriginalFilename() : "null") +
                ", contentHyperLink='" + contentHyperLink + '\'' +
                '}';
    }
}
