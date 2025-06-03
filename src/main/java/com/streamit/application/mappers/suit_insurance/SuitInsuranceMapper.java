package com.streamit.application.mappers.suit_insurance;

import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.suit_insurance.*;

public class SuitInsuranceMapper {
    public static SuitInsuranceCreateWithContentDTO mapSuitInsuranceCreateToSuitInsuranceCreateWithDTO(SuitInsuranceCreateDTO suitInsuranceCreateDTO, Content content) {
        return new SuitInsuranceCreateWithContentDTO(
                suitInsuranceCreateDTO.getTitleTh(),
                suitInsuranceCreateDTO.getTitleEn(),
                suitInsuranceCreateDTO.getImagePath(),
                content.getId()
        );
    }

    public static SuitInsuranceResDTO mapSuitInsuranceToSuitInsuranceResDTO(SuitInsurance suitInsurance, Content content) {
        return new SuitInsuranceResDTO(
                suitInsurance.getId(),

                content.getTitle(),
                content.getEffectiveFrom(),
                content.getEffectiveTo(),
                content.getStatus(),
                CategoryEnum.SUIT_INSURANCE,

                suitInsurance.getTitleTh(),
                suitInsurance.getTitleEn(),
                suitInsurance.getImagePath()
        );
    }

    public static SuitInsuranceCreateDTO mapSuitInsuranceCreateReqToSuitInsuranceCreateDTO(SuitInsuranceCreateReqDTO suitInsuranceCreateReqDTO, String imagePath){
        return new SuitInsuranceCreateDTO(
                suitInsuranceCreateReqDTO.getTitleTh(),
                suitInsuranceCreateReqDTO.getTitleEn(),
                imagePath
        );
    }
}
