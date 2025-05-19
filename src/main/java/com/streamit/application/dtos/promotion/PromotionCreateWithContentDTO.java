package com.streamit.application.dtos.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PromotionCreateWithContentDTO {
    private String titleTh;
    private String titleEn;
    private String descriptionTh;
    private String descriptionEn;
    private String coverImagePath;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private UUID contentId;
}