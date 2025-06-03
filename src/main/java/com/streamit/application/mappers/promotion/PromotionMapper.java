package com.streamit.application.mappers.promotion;

import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.Content;
import com.streamit.application.dtos.promotion.*;

import java.time.LocalDateTime;

public class PromotionMapper {
    public static PromotionResDTO mapPromotionToPromotionResDTO(Promotion promotion, Content content) {
        return new PromotionResDTO(
                promotion.getId(),
                content.getTitle(),
                content.getEffectiveFrom(),
                content.getEffectiveTo(),
                content.getStatus(),
                CategoryEnum.PROMOTION,

                promotion.getTitleTh(),
                promotion.getTitleEn(),
                promotion.getDescriptionTh(),
                promotion.getDescriptionEn(),
                promotion.getCoverImagePath(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getCreatedAt(),
                promotion.getUpdatedAt(),
                promotion.getDeletedAt()
        );
    }

    public static PromotionCreateWithContentDTO mapPromotionCreateToPromotionCreateWithContentDTO(PromotionCreateDTO promotionCreateDTO, Content content) {
        return new PromotionCreateWithContentDTO(
                promotionCreateDTO.getTitleTh(),
                promotionCreateDTO.getTitleEn(),
                promotionCreateDTO.getDescriptionTh(),
                promotionCreateDTO.getDescriptionEn(),
                promotionCreateDTO.getCoverImagePath(),
                promotionCreateDTO.getStartDate(),
                promotionCreateDTO.getEndDate(),
                content.getId()
        );
    }

    public static PromotionCreateDTO mapPromotionCreateReqToPromotionCreateDTO(PromotionCreateReqDTO promotionCreateReqDTO,
                                                                               String coverImagePath) {
        return new PromotionCreateDTO(
                promotionCreateReqDTO.getTitleTh(),
                promotionCreateReqDTO.getTitleEn(),
                promotionCreateReqDTO.getDescriptionTh(),
                promotionCreateReqDTO.getDescriptionEn(),
                coverImagePath,
                LocalDateTime.parse(promotionCreateReqDTO.getStartDate()),
                LocalDateTime.parse(promotionCreateReqDTO.getEndDate())
        );
    }
}