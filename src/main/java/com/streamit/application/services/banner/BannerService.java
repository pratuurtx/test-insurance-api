package com.streamit.application.services.banner;

import com.streamit.application.daos.banner.BannerDAO;
import com.streamit.application.dtos.banner.*;
import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.TypeEnum;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.services.minio.MinioService;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
public class BannerService {
    private final BannerDAO bannerDAO;
    private final MinioService minioService;

    public BannerService(BannerDAO bannerDAO, MinioService minioService) {
        this.bannerDAO = bannerDAO;
        this.minioService = minioService;
    }

    public BannerResDTO createBanner(BannerCreateReqDTO bannerCreateReqDTO) {
        try {
            String coverImagePath = minioService.uploadFile(bannerCreateReqDTO.getCoverImage(), bannerCreateReqDTO.getCoverImage().getOriginalFilename());
            BannerCreateDTO bannerCreateDTO = new BannerCreateDTO(
                    coverImagePath,
                    bannerCreateReqDTO.getCoverHyperLink()
            );
            List<BannerContentCreateDTO> bannerContentCreateDTOs = new ArrayList<>();
            for (int idx = 0; idx < bannerCreateReqDTO.getContents().size(); idx++) {
                try {
                    String imagePath = minioService.uploadFile(bannerCreateReqDTO.getContents().get(idx).getContentImage(), bannerCreateReqDTO.getContents().get(idx).getContentImage().getOriginalFilename());
                    bannerContentCreateDTOs.add(new BannerContentCreateDTO(coverImagePath, imagePath));
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                    throw new BadRequestException(ex.getMessage());
                }
            }

            ContentCreateDTO contentCreateDTO = new ContentCreateDTO(
                    bannerCreateReqDTO.getTitle(),
                    StatusEnum.fromValue(bannerCreateReqDTO.getStatus()),
                    TypeEnum.BANNER,
                    bannerCreateReqDTO.getEffectiveFrom(),
                    bannerCreateReqDTO.getEffectiveTo()
            );

            return bannerDAO.insertBanner(bannerCreateDTO, contentCreateDTO, bannerContentCreateDTOs);
        } catch (MinioException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<BannerResDTO> getAllBanners() {
        try {
            return bannerDAO.findAllBannerWithContents();
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public BannerResDTO getBannerById(UUID bannerId) {
        try {
            return bannerDAO.findBannerWithContentsById(bannerId);
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public BannerResDTO updateBannerById(UUID id, BannerUpdateReqDTO bannerUpdateReqDTO) {
        try {
            Banner banner = bannerDAO.findById(id).orElseThrow(() -> new NotFoundException(id.toString()));
            Map<String, Object> contentFieldMap = new HashMap<>();
            if (bannerUpdateReqDTO.getTitle() != null) {
                contentFieldMap.put("title", bannerUpdateReqDTO.getTitle());
            }

            if (bannerUpdateReqDTO.getEffectiveFrom() != null) {
                contentFieldMap.put("effectiveFrom", bannerUpdateReqDTO.getEffectiveFrom());
            }

            if (bannerUpdateReqDTO.getEffectiveTo() != null) {
                contentFieldMap.put("effectiveTo", bannerUpdateReqDTO.getEffectiveTo());
            }

            if (bannerUpdateReqDTO.getStatus() != null) {
                contentFieldMap.put("status", StatusEnum.valueOf(bannerUpdateReqDTO.getStatus()));
            }

            Map<String, Object> bannerFieldMap = new HashMap<>();

            if (bannerUpdateReqDTO.getCoverHyperLink() != null) {
                bannerFieldMap.put("coverHyperLink", bannerUpdateReqDTO.getCoverHyperLink());
            }

            if (bannerUpdateReqDTO.getCoverImage() != null) {
                try {
                    String coverImagePath = minioService.uploadFile(bannerUpdateReqDTO.getCoverImage(), bannerUpdateReqDTO.getCoverImage().getOriginalFilename());
                    bannerFieldMap.put("coverImagePath", coverImagePath);
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                    throw new BadRequestException(ex.getMessage());
                }
            }

            List<BannerContentUpdateDTO> bannerContentUpdateDTO = new ArrayList<>();
            if (bannerUpdateReqDTO.getContentUpdates() != null) {
                bannerContentUpdateDTO = bannerUpdateReqDTO.getContentUpdates()
                        .stream()
                        .map(bannerContentUpdate -> {
                            Map<String, Object> bannerContentFieldMap = new HashMap<>();
                            try {
                                if (bannerContentUpdate.getContentImage() != null) {
                                    String contentImagePath = minioService.uploadFile(bannerContentUpdate.getContentImage(), bannerContentUpdate.getContentImage().getOriginalFilename());
                                    bannerContentFieldMap.put("contentImagePath", contentImagePath);
                                }

                                if (bannerContentUpdate.getContentHyperLink() != null) {
                                    bannerContentFieldMap.put("contentHyperLink", bannerContentUpdate.getContentHyperLink());
                                }
                            } catch (MinioException ex) {
                                log.error(ex.getMessage());
                                throw new BadRequestException(ex.getMessage());
                            }

                            return new BannerContentUpdateDTO(
                                    UUID.fromString(bannerContentUpdate.getId()),
                                    bannerContentFieldMap
                            );
                        })
                        .toList();
            }

            List<UUID> bannerContentUUIDRemoves = new ArrayList<>();

            if (bannerUpdateReqDTO.getContentRemoves() != null) {
                bannerContentUUIDRemoves = bannerUpdateReqDTO.getContentRemoves().stream()
                        .map(UUID::fromString)
                        .toList();
            }

            List<BannerContentCreateDTO> bannerContentCreateDTOs = new ArrayList<>();
            if (bannerUpdateReqDTO.getContentCreates() != null) {
                bannerContentCreateDTOs = bannerUpdateReqDTO.getContentCreates().stream()
                        .map(bannerContentCreate -> {
                            try {
                                String contentImagePath = minioService.uploadFile(bannerContentCreate.getContentImage(), bannerContentCreate.getContentImage().getOriginalFilename());
                                return new BannerContentCreateDTO(contentImagePath, bannerContentCreate.getContentHyperLink());
                            } catch (MinioException ex) {
                                log.error(ex.getMessage());
                                throw new BadRequestException(ex.getMessage());
                            }
                        })
                        .toList();
            }

            BannerUpdateDTO bannerUpdateDTO = new BannerUpdateDTO(
                    bannerFieldMap,
                    contentFieldMap,
                    bannerContentUpdateDTO,
                    bannerContentCreateDTOs,
                    bannerContentUUIDRemoves
            );

            BannerResDTO updatedBanner = bannerDAO.updateBanner(id, bannerUpdateDTO);

            // delete old cover image if updated new cover image
            if (bannerUpdateReqDTO.getCoverImage() != null) {
                minioService.deleteFile(banner.getCoverImagePath());
            }

            for (int idx = 0; idx < bannerUpdateReqDTO.getContentUpdates().size(); idx++) {
                try {
                    BannerContentResDTO bannerContentResDTO = bannerDAO.findBannerContentByBannerContentId(UUID.fromString(bannerUpdateReqDTO.getContentUpdates().get(idx).getId()));
                    minioService.deleteFile(bannerContentResDTO.getContentImagePath());
                } catch (SQLException ex) {
                    log.error(ex.getMessage());
                }
            }

            return updatedBanner;
        } catch (SQLException | MinioException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public boolean deleteBannerById(UUID id) {
        try {
            Banner banner = bannerDAO.findById(id).orElseThrow(() -> new NotFoundException("Banner was not found."));
            BannerResDTO bannerResDTO = bannerDAO.findBannerWithContentsById(id);
            boolean deletedBanner = bannerDAO.deleteBanner(id, banner.getContentId());
            if (deletedBanner) {
                minioService.deleteFile(bannerResDTO.getCoverImagePath());
                for (int idx = 0; idx < bannerResDTO.getContents().size(); idx++) {
                    minioService.deleteFile(bannerResDTO.getContents().get(idx).getContentImagePath());
                }
            }
            return deletedBanner;
        } catch (SQLException | MinioException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }
}
