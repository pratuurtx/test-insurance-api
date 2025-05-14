package com.streamit.application.dtos.insurance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class InsuranceCreateWithContentDTO {
    private String titleTh;
    private String titleEn;
    private String descriptionTh;
    private String descriptionEn;
    private String coverImagePath;
    private String iconImagePath;
    private UUID contentId;
}
