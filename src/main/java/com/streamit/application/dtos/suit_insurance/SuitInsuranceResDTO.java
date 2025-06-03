package com.streamit.application.dtos.suit_insurance;

import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.common.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SuitInsuranceResDTO {
    private UUID id;

    private String title;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private StatusEnum status;
    private CategoryEnum category;

    private String titleTh;
    private String titleEn;
    private String imagePath;
}
