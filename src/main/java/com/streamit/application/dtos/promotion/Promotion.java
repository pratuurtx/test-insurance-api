package com.streamit.application.dtos.promotion;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Promotion {
    private UUID id;
    private String coverImagePath;
    private String titleTh;
    private String titleEn;
    private String descriptionTh;
    private String descriptionEn;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private UUID contentId;
}