package com.streamit.application.services.suit_insurance;

import com.streamit.application.daos.content.ContentDAO;
import com.streamit.application.daos.suit_insurance.SuitInsuranceDAO;
import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.dtos.suit_insurance.*;
import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.mappers.suit_insurance.SuitInsuranceMapper;
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
public class SuitInsuranceService {
    private final SuitInsuranceDAO suitInsuranceDAO;
    private final MinioService minioService;

    public SuitInsuranceService(SuitInsuranceDAO suitInsuranceDAO, MinioService minioService) {
        this.suitInsuranceDAO = suitInsuranceDAO;
        this.minioService = minioService;
    }

    public SuitInsuranceResDTO createSuitInsurance(SuitInsuranceCreateReqDTO suitInsuranceReqDTO) {
        try {
            String imagePath = minioService.uploadFile(suitInsuranceReqDTO.getImage(), suitInsuranceReqDTO.getImage().getOriginalFilename());
            SuitInsuranceCreateDTO suitInsuranceCreateDTO = SuitInsuranceMapper.mapSuitInsuranceCreateReqToSuitInsuranceCreateDTO(suitInsuranceReqDTO, imagePath);
            ContentCreateDTO contentCreateDTO = new ContentCreateDTO(
                    suitInsuranceReqDTO.getTitle(),
                    StatusEnum.fromValue(suitInsuranceReqDTO.getStatus()),
                    CategoryEnum.SUIT_INSURANCE,
                    LocalDateTime.parse(suitInsuranceReqDTO.getEffectiveFrom()),
                    LocalDateTime.parse(suitInsuranceReqDTO.getEffectiveTo())
            );
            return suitInsuranceDAO.insertSuitInsurance(suitInsuranceCreateDTO, contentCreateDTO);
        } catch (MinioException | SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public List<SuitInsuranceResDTO> getAllSuitInsurances() {
        try {
            return suitInsuranceDAO.findAllSuitInsurancesWithContent();
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public SuitInsuranceResDTO getSuitInsuranceById(UUID id) {
        try {
            return suitInsuranceDAO.findSuitInsuranceWithContentById(id);
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(String.format(ex.getMessage()));
        }
    }

    public SuitInsuranceResDTO updateSuitInsuranceWithContentById(UUID id, SuitInsuranceUpdateReqDTO suitInsuranceUpdateReqDTO) {
        try {
            Map<String, Object> suitInsuranceUpdateMap = new HashMap<>();
            SuitInsurance suitInsurance = suitInsuranceDAO.findById(id).orElseThrow(() -> new NotFoundException("Suit insurance with ID#" + id.toString() + " was not found."));
            if (suitInsuranceUpdateReqDTO.getImage() != null) {
                String imagePath = minioService.uploadFile(suitInsuranceUpdateReqDTO.getImage(), suitInsuranceUpdateReqDTO.getImage().getOriginalFilename());
                suitInsuranceUpdateMap.put("imagePath", imagePath);
            }
            if (suitInsuranceUpdateReqDTO.getTitleTh() != null) {
                suitInsuranceUpdateMap.put("titleTh", suitInsuranceUpdateReqDTO.getTitleTh());
            }
            if (suitInsuranceUpdateReqDTO.getTitleEn() != null) {
                suitInsuranceUpdateMap.put("titleEn", suitInsuranceUpdateReqDTO.getTitleEn());
            }

            Map<String, Object> contentUpdateMap = new HashMap<>();
            if (suitInsuranceUpdateReqDTO.getTitle() != null) {
                contentUpdateMap.put("title", suitInsuranceUpdateReqDTO.getTitle());
            }
            if (suitInsuranceUpdateReqDTO.getStatus() != null) {
                contentUpdateMap.put("status", StatusEnum.fromValue(suitInsuranceUpdateReqDTO.getStatus()));
            }
            if (suitInsuranceUpdateReqDTO.getEffectiveFrom() != null) {
                contentUpdateMap.put("effectiveFrom", LocalDateTime.parse(suitInsuranceUpdateReqDTO.getEffectiveFrom()));
            }

            if (suitInsuranceUpdateReqDTO.getEffectiveTo() != null) {
                contentUpdateMap.put("effectiveTo", LocalDateTime.parse(suitInsuranceUpdateReqDTO.getEffectiveTo()));
            }

            if (!contentUpdateMap.isEmpty()) {
                contentUpdateMap.put("updatedAt", LocalDateTime.now());
            }

            SuitInsuranceUpdateDTO suitInsuranceUpdateDTO = new SuitInsuranceUpdateDTO(suitInsuranceUpdateMap, contentUpdateMap);
            SuitInsuranceResDTO updatedSuitInsurance = suitInsuranceDAO.updateSuitInsuranceById(id, suitInsuranceUpdateDTO);
            if (suitInsuranceUpdateReqDTO.getImage() != null) {
                minioService.deleteFile(suitInsurance.getImagePath());
            }
            return updatedSuitInsurance;

        } catch (SQLException | MinioException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public boolean deleteSuitInsuranceById(UUID id) {
        try {
            SuitInsurance suitInsurance = suitInsuranceDAO.findById(id).orElseThrow(() -> new NotFoundException("Suit insurance was not found."));
            boolean deletedSuitInsurance = suitInsuranceDAO.deleteSuitInsuranceById(id, suitInsurance.getContentId());
            if (deletedSuitInsurance) {
                try {
                    minioService.deleteFile(suitInsurance.getImagePath());
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                }
            }
            return true;
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }
}
