package com.streamit.application.services.insurance;

import com.streamit.application.daos.insurance.InsuranceDAO;
import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.dtos.insurance.*;
import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.mappers.insurance.InsuranceMapper;
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
public class InsuranceService {
    private final MinioService minioService;
    private final InsuranceDAO insuranceDAO;

    public InsuranceService(MinioService minioService, InsuranceDAO insuranceDAO) {
        this.minioService = minioService;
        this.insuranceDAO = insuranceDAO;

    }

    public InsuranceResDTO createInsurance(
            InsuranceCreateReqDTO insuranceCreateReqDTO
    ) {
        try {
            String coverImagePath = minioService.uploadFile(insuranceCreateReqDTO.getCoverImage(), insuranceCreateReqDTO.getCoverImage().getOriginalFilename());
            String iconImagePath = minioService.uploadFile(insuranceCreateReqDTO.getIconImage(), insuranceCreateReqDTO.getIconImage().getOriginalFilename());
            InsuranceCreateDTO insuranceCreateDTO = InsuranceMapper.mapInsuranceCreateReqToInsuranceCreateDTO(insuranceCreateReqDTO, coverImagePath, iconImagePath);
            ContentCreateDTO contentCreateDTO = new ContentCreateDTO(
                    insuranceCreateReqDTO.getTitle(),
                    StatusEnum.fromValue(insuranceCreateReqDTO.getStatus()),
                    CategoryEnum.INSURANCE,
                    LocalDateTime.parse(insuranceCreateReqDTO.getEffectiveFrom()),
                    LocalDateTime.parse(insuranceCreateReqDTO.getEffectiveTo())
            );
            return insuranceDAO.insertInsurance(insuranceCreateDTO, contentCreateDTO);
        } catch (MinioException | SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public List<InsuranceResDTO> getInsurances() {
        try {
            return insuranceDAO.findAllInsurancesWithContent();
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public InsuranceResDTO getInsuranceById(UUID id) {
        try {
            return insuranceDAO.findInsuranceWithContentById(id);
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public InsuranceResDTO updateInsurance(UUID id, InsuranceUpdateReqDTO insuranceUpdateReqDTO) {
        try {

            Insurance insuranceResDTO = insuranceDAO.findById(id).orElseThrow(() -> new NotFoundException("Insurance was not found."));

            // insurance field map
            Map<String, Object> insuranceUpdateMap = new HashMap<>();
            if (insuranceUpdateReqDTO.getTitleTh() != null) {
                insuranceUpdateMap.put("titleTh", insuranceUpdateReqDTO.getTitleTh());
            }

            if (insuranceUpdateReqDTO.getTitleEn() != null) {
                insuranceUpdateMap.put("titleEn", insuranceUpdateReqDTO.getTitleEn());
            }

            if (insuranceUpdateReqDTO.getDescriptionTh() != null) {
                insuranceUpdateMap.put("descriptionTh", insuranceUpdateReqDTO.getDescriptionTh());
            }

            if (insuranceUpdateReqDTO.getDescriptionEn() != null) {
                insuranceUpdateMap.put("descriptionEn", insuranceUpdateReqDTO.getDescriptionEn());
            }

            if (insuranceUpdateReqDTO.getCoverImage() != null) {
                String coverImagePath = minioService.uploadFile(insuranceUpdateReqDTO.getCoverImage(), insuranceUpdateReqDTO.getCoverImage().getOriginalFilename());
                insuranceUpdateMap.put("coverImagePath", coverImagePath);
            }

            if (insuranceUpdateReqDTO.getIconImage() != null) {
                String iconImagePath = minioService.uploadFile(insuranceUpdateReqDTO.getIconImage(), insuranceUpdateReqDTO.getIconImage().getOriginalFilename());
                insuranceUpdateMap.put("iconImagePath", iconImagePath);
            }

            if (!insuranceUpdateMap.isEmpty()) {
                insuranceUpdateMap.put("updatedAt", LocalDateTime.now());
            }
            //

            // content field map
            Map<String, Object> contentUpdateMap = new HashMap<>();
            if (insuranceUpdateReqDTO.getTitle() != null) {
                contentUpdateMap.put("title", insuranceUpdateReqDTO.getTitle());
            }

            if (insuranceUpdateReqDTO.getStatus() != null) {
                contentUpdateMap.put("status", StatusEnum.fromValue(insuranceUpdateReqDTO.getStatus()));
            }

            if (insuranceUpdateReqDTO.getEffectiveFrom() != null) {
                contentUpdateMap.put("effectiveFrom", LocalDateTime.parse(insuranceUpdateReqDTO.getEffectiveFrom()));
            }

            if (insuranceUpdateReqDTO.getEffectiveTo() != null) {
                contentUpdateMap.put("effectiveTo", LocalDateTime.parse(insuranceUpdateReqDTO.getEffectiveTo()));
            }

            if (!contentUpdateMap.isEmpty()) {
                contentUpdateMap.put("updatedAt", LocalDateTime.now());
            }
            //

            InsuranceUpdateDTO updateInsuranceUpdateDTO = new InsuranceUpdateDTO(
                    insuranceUpdateMap,
                    contentUpdateMap
            );
            InsuranceResDTO updatedInsuranceResDTO = insuranceDAO.updateInsurance(id, updateInsuranceUpdateDTO);

            if (insuranceUpdateReqDTO.getCoverImage() != null) {
                try {
                    minioService.deleteFile(insuranceResDTO.getCoverImagePath());
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                }
            }

            if (insuranceUpdateReqDTO.getIconImage() != null) {
                try {
                    minioService.deleteFile(insuranceResDTO.getIconImagePath());
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                }
            }
            return updatedInsuranceResDTO;
        } catch (SQLException | MinioException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    // soft delete set deletedAt field to datetime
    public boolean softDeleteInsuranceById(UUID id) {
        try {
            insuranceDAO.findById(id).orElseThrow(() -> new NotFoundException("Insurance was not found."));
            LocalDateTime deletedAt = LocalDateTime.now();
            Map<String, Object> deletedAtMap = Map.of("deletedAt", deletedAt);
            InsuranceUpdateDTO updateInsuranceUpdateDTO = new InsuranceUpdateDTO(deletedAtMap, deletedAtMap);

            insuranceDAO.updateInsurance(id, updateInsuranceUpdateDTO);
            return true;
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public boolean deleteInsuranceById(UUID id) {
        try {
            Insurance insurance = insuranceDAO.findById(id).orElseThrow(() -> new NotFoundException("Insurance was not found."));
            boolean deletedInsurance = insuranceDAO.deleteInsuranceById(id, insurance.getContentId());
            if (deletedInsurance) {
                try {
                    minioService.deleteFile(insurance.getCoverImagePath());
                    minioService.deleteFile(insurance.getIconImagePath());
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                }
            }
            return deletedInsurance;
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }
}
