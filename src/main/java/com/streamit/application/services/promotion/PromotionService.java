package com.streamit.application.services.promotion;

import com.streamit.application.daos.promotion.PromotionDAO;
import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.dtos.promotion.*;
import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.mappers.promotion.PromotionMapper;
import com.streamit.application.services.minio.MinioService;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PromotionService {
    private final MinioService minioService;
    private final PromotionDAO promotionDAO;

    public PromotionService(MinioService minioService, PromotionDAO promotionDAO) {
        this.minioService = minioService;
        this.promotionDAO = promotionDAO;
    }

    public PromotionResDTO createPromotion(
            PromotionCreateReqDTO promotionCreateReqDTO
    ) {
        try {
            String coverImagePath = minioService.uploadFile(promotionCreateReqDTO.getCoverImage(), promotionCreateReqDTO.getCoverImage().getOriginalFilename());
            PromotionCreateDTO promotionCreateDTO = PromotionMapper.mapPromotionCreateReqToPromotionCreateDTO(promotionCreateReqDTO, coverImagePath);
            ContentCreateDTO contentCreateDTO = new ContentCreateDTO(
                    promotionCreateReqDTO.getTitle(),
                    StatusEnum.fromValue(promotionCreateReqDTO.getStatus()),
                    CategoryEnum.PROMOTION,
                    LocalDateTime.parse(promotionCreateReqDTO.getEffectiveFrom()),
                    LocalDateTime.parse(promotionCreateReqDTO.getEffectiveTo())
            );
            return promotionDAO.insertPromotion(promotionCreateDTO, contentCreateDTO);
        } catch (MinioException | SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public List<PromotionResDTO> getPromotions() {
        try {
            return promotionDAO.findAllPromotionsWithContent();
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public PromotionResDTO getPromotionById(UUID id) {
        try {
            return promotionDAO.findPromotionWithContentById(id);
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public PromotionResDTO updatePromotion(UUID id, PromotionUpdateReqDTO promotionUpdateReqDTO) {
        try {
            Promotion promotionResDTO = promotionDAO.findById(id).orElseThrow(() -> new NotFoundException("Promotion was not found."));

            // promotion field map
            Map<String, Object> promotionUpdateMap = new HashMap<>();
            if (promotionUpdateReqDTO.getTitleTh() != null) {
                promotionUpdateMap.put("titleTh", promotionUpdateReqDTO.getTitleTh());
            }

            if (promotionUpdateReqDTO.getTitleEn() != null) {
                promotionUpdateMap.put("titleEn", promotionUpdateReqDTO.getTitleEn());
            }

            if (promotionUpdateReqDTO.getDescriptionTh() != null) {
                promotionUpdateMap.put("descriptionTh", promotionUpdateReqDTO.getDescriptionTh());
            }

            if (promotionUpdateReqDTO.getDescriptionEn() != null) {
                promotionUpdateMap.put("descriptionEn", promotionUpdateReqDTO.getDescriptionEn());
            }

            if (promotionUpdateReqDTO.getCoverImage() != null) {
                String coverImagePath = minioService.uploadFile(promotionUpdateReqDTO.getCoverImage(), promotionUpdateReqDTO.getCoverImage().getOriginalFilename());
                promotionUpdateMap.put("coverImagePath", coverImagePath);
            }

            if (promotionUpdateReqDTO.getStartDate() != null) {
                promotionUpdateMap.put("startDate", LocalDateTime.parse(promotionUpdateReqDTO.getStartDate()));
            }

            if (promotionUpdateReqDTO.getEndDate() != null) {
                promotionUpdateMap.put("endDate", LocalDateTime.parse(promotionUpdateReqDTO.getEndDate()));
            }

            if (!promotionUpdateMap.isEmpty()) {
                promotionUpdateMap.put("updatedAt", LocalDateTime.now());
            }
            //

            // content field map
            Map<String, Object> contentUpdateMap = new HashMap<>();
            if (promotionUpdateReqDTO.getTitle() != null) {
                contentUpdateMap.put("title", promotionUpdateReqDTO.getTitle());
            }

            if (promotionUpdateReqDTO.getStatus() != null) {
                contentUpdateMap.put("status", StatusEnum.fromValue(promotionUpdateReqDTO.getStatus()));
            }

            if (promotionUpdateReqDTO.getEffectiveFrom() != null) {
                contentUpdateMap.put("effectiveFrom", LocalDateTime.parse(promotionUpdateReqDTO.getEffectiveFrom()));
            }

            if (promotionUpdateReqDTO.getEffectiveTo() != null) {
                contentUpdateMap.put("effectiveTo", LocalDateTime.parse(promotionUpdateReqDTO.getEffectiveTo()));
            }

            if (!contentUpdateMap.isEmpty()) {
                contentUpdateMap.put("updatedAt", LocalDateTime.now());
            }
            //

            PromotionUpdateDTO updatePromotionUpdateDTO = new PromotionUpdateDTO(
                    promotionUpdateMap,
                    contentUpdateMap
            );
            PromotionResDTO updatedPromotionResDTO = promotionDAO.updatePromotion(id, updatePromotionUpdateDTO);

            if (promotionUpdateReqDTO.getCoverImage() != null) {
                try {
                    minioService.deleteFile(promotionResDTO.getCoverImagePath());
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                }
            }

            return updatedPromotionResDTO;
        } catch (SQLException | MinioException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    // soft delete set deletedAt field to datetime
    public boolean softDeletePromotionById(UUID id) {
        try {
            promotionDAO.findById(id).orElseThrow(() -> new NotFoundException("Promotion was not found."));
            LocalDateTime deletedAt = LocalDateTime.now();
            Map<String, Object> deletedAtMap = Map.of("deletedAt", deletedAt);
            PromotionUpdateDTO updatePromotionUpdateDTO = new PromotionUpdateDTO(deletedAtMap, deletedAtMap);

            promotionDAO.updatePromotion(id, updatePromotionUpdateDTO);
            return true;
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public boolean deletePromotionById(UUID id) {
        try {
            Promotion promotion = promotionDAO.findById(id).orElseThrow(() -> new NotFoundException("Promotion was not found."));
            boolean deletedPromotion = promotionDAO.deletePromotionById(id, promotion.getContentId());
            if (deletedPromotion) {
                try {
                    minioService.deleteFile(promotion.getCoverImagePath());
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                }
            }
            return deletedPromotion;
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }
}