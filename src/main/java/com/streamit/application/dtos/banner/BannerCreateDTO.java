package com.streamit.application.dtos.banner;

import com.streamit.application.dtos.common.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BannerCreateDTO {
    private String coverImagePath;
    private String coverHyperLink;
}
