package com.streamit.application.dtos.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BannerContentResDTO {
    private UUID id;
    private String contentImagePath;
    private String contentHyperLink;
}
