package com.streamit.application.dtos.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BannerCreateWithContentDTO {
    private String coverImagePath;
    private String coverHyperLink;
    private UUID contentId;
}
