package com.streamit.application.dtos.banner;

import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.common.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class BannerResDTO {
    private UUID id;

    private String title;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private StatusEnum status;
    private CategoryEnum category;
    private String coverImagePath;
    private String coverHyperLink;
    private List<BannerContentResDTO> contents;
}
