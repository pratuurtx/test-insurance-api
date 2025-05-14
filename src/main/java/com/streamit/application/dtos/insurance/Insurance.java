package com.streamit.application.dtos.insurance;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Insurance {
    private UUID id;
    private String titleTh;
    private String titleEn;
    private String descriptionTh;
    private String descriptionEn;
    private String coverImagePath;
    private String iconImagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private UUID contentId;
}
