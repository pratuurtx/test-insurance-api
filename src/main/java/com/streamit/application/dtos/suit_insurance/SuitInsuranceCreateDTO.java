package com.streamit.application.dtos.suit_insurance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuitInsuranceCreateDTO {
    private String titleTh;
    private String titleEn;
    private String imagePath;
}
