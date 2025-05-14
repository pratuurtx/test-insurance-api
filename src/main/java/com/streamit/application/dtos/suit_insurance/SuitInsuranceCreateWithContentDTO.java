package com.streamit.application.dtos.suit_insurance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SuitInsuranceCreateWithContentDTO {
    private String titleTh;
    private String titleEn;
    private String imagePath;
    private UUID contentId;
}
