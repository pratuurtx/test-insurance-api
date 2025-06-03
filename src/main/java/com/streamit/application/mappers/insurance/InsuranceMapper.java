package com.streamit.application.mappers.insurance;

import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.insurance.*;

public class InsuranceMapper {
    public static InsuranceResDTO mapInsuranceToInsuranceResDTO(Insurance insurance, Content content) {
        return new InsuranceResDTO(
                insurance.getId(),
                content.getTitle(),
                content.getEffectiveFrom(),
                content.getEffectiveTo(),
                content.getStatus(),
                CategoryEnum.INSURANCE,

                insurance.getTitleTh(),
                insurance.getTitleEn(),
                insurance.getDescriptionTh(),
                insurance.getDescriptionEn(),
                insurance.getCoverImagePath(),
                insurance.getIconImagePath(),
                insurance.getCreatedAt(),
                insurance.getUpdatedAt(),
                insurance.getDeletedAt()
        );
    }

    public static InsuranceCreateWithContentDTO mapInsuranceCreateToInsuranceCreateWithContentDTO(InsuranceCreateDTO insuranceCreateDTO, Content content) {
        return new InsuranceCreateWithContentDTO(
                insuranceCreateDTO.getTitleTh(),
                insuranceCreateDTO.getTitleEn(),
                insuranceCreateDTO.getDescriptionTh(),
                insuranceCreateDTO.getDescriptionEn(),
                insuranceCreateDTO.getCoverImagePath(),
                insuranceCreateDTO.getIconImagePath(),
                content.getId()
        );
    }

    public static InsuranceCreateDTO mapInsuranceCreateReqToInsuranceCreateDTO(InsuranceCreateReqDTO insuranceCreateReqDTO,
                                                                               String coverImagePath, String iconImagePath) {
        return new InsuranceCreateDTO(
                insuranceCreateReqDTO.getTitleTh(),
                insuranceCreateReqDTO.getTitleEn(),
                insuranceCreateReqDTO.getDescriptionTh(),
                insuranceCreateReqDTO.getDescriptionEn(),
                coverImagePath,
                iconImagePath
        );
    }
}
