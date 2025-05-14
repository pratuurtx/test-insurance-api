package com.streamit.application.dtos.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BannerContentCreateDTO {
    private String contentImagePath;
    private String contentHyperLink;
}
