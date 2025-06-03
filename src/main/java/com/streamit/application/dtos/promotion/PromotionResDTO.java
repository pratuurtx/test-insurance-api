package com.streamit.application.dtos.promotion;

import com.streamit.application.dtos.common.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PromotionResDTO {
    private UUID id;

    private String title;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private StatusEnum status;

    private String titleTh;
    private String titleEn;
    private String descriptionTh;
    private String descriptionEn;
    private String coverImagePath;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}