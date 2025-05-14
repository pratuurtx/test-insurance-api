package com.streamit.application.dtos.banner;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BannerContent {
    private UUID id;
    private UUID bannerId;
    private String contentImagePath;
    private String contentHyperLink;
}
