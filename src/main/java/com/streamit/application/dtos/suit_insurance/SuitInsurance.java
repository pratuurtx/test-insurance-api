package com.streamit.application.dtos.suit_insurance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class SuitInsurance {
    private UUID id;
    private String titleTh;
    private String titleEn;
    private String imagePath;
    private UUID contentId;
}
