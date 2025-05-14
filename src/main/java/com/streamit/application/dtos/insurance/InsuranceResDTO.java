package com.streamit.application.dtos.insurance;

import com.streamit.application.dtos.common.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class InsuranceResDTO {
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
    private String iconImagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
