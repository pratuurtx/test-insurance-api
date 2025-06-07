package com.streamit.application.services.banner;

import com.streamit.application.daos.banner.BannerDAO;
import com.streamit.application.dtos.banner.*;
import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.ContentCreateDTO;
import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.exceptions.NotFoundException;
import com.streamit.application.services.minio.MinioService;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
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
                    String contentImagePath = minioService.uploadFile(bannerCreateReqDTO.getContents().get(idx).getContentImage(), bannerCreateReqDTO.getContents().get(idx).getContentImage().getOriginalFilename());
                    bannerContentCreateDTOs.add(new BannerContentCreateDTO(contentImagePath, bannerCreateReqDTO.getContents().get(idx).getContentHyperLink()));
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                    throw new BadRequestException(ex.getMessage());
                }
            }

            ContentCreateDTO contentCreateDTO = new ContentCreateDTO(
                    bannerCreateReqDTO.getTitle(),
                    StatusEnum.fromValue(bannerCreateReqDTO.getStatus()),
                    CategoryEnum.BANNER,
                    LocalDateTime.parse(bannerCreateReqDTO.getEffectiveFrom()),
                    LocalDateTime.parse(bannerCreateReqDTO.getEffectiveTo())
            );

            return bannerDAO.insertBanner(bannerCreateDTO, contentCreateDTO, bannerContentCreateDTOs);
        } catch (MinioException | SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
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
            Map<String, Object> contentUpdateMap = new HashMap<>();
            if (bannerUpdateReqDTO.getTitle() != null) {
                contentUpdateMap.put("title", bannerUpdateReqDTO.getTitle());
            }

            if (bannerUpdateReqDTO.getEffectiveFrom() != null) {
                contentUpdateMap.put("effectiveFrom", LocalDateTime.parse(bannerUpdateReqDTO.getEffectiveFrom()));
            }

            if (bannerUpdateReqDTO.getEffectiveTo() != null) {
                contentUpdateMap.put("effectiveTo", LocalDateTime.parse(bannerUpdateReqDTO.getEffectiveTo()));
            }

            if (bannerUpdateReqDTO.getStatus() != null) {
                contentUpdateMap.put("status", StatusEnum.valueOf(bannerUpdateReqDTO.getStatus()));
            }

            if (!contentUpdateMap.isEmpty()) {
                contentUpdateMap.put("updatedAt", LocalDateTime.now());
            }
            List<String> oldContentImagePathList = new ArrayList<>();
            Map<String, Object> bannerUpdateMap = new HashMap<>();

            if (bannerUpdateReqDTO.getCoverHyperLink() != null) {
                bannerUpdateMap.put("coverHyperLink", bannerUpdateReqDTO.getCoverHyperLink());
            }

            if (bannerUpdateReqDTO.getCoverImage() != null) {
                try {
                    String coverImagePath = minioService.uploadFile(bannerUpdateReqDTO.getCoverImage(), bannerUpdateReqDTO.getCoverImage().getOriginalFilename());
                    bannerUpdateMap.put("coverImagePath", coverImagePath);
                    oldContentImagePathList.add(banner.getCoverImagePath());
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
                            Map<String, Object> bannercontentUpdateMap = new HashMap<>();
                            try {
                                if (bannerContentUpdate.getId() != null) {
                                    if (bannerContentUpdate.getContentImage() != null) {
                                        BannerContentResDTO bannerContentResDTO = this.bannerDAO.findBannerContentByBannerContentId(UUID.fromString(bannerContentUpdate.getId()));
                                        log.info("banner content update: {}", bannerContentUpdate.getContentImage().getOriginalFilename());
                                        String contentImagePath = minioService.uploadFile(bannerContentUpdate.getContentImage(), bannerContentUpdate.getContentImage().getOriginalFilename());
                                        log.info("after update {}", contentImagePath);
                                        bannercontentUpdateMap.put("contentImagePath", contentImagePath);

                                        oldContentImagePathList.add(bannerContentResDTO.getContentImagePath());
                                    }

                                    if (bannerContentUpdate.getContentHyperLink() != null) {
                                        bannercontentUpdateMap.put("contentHyperLink", bannerContentUpdate.getContentHyperLink());
                                    }
                                }
                            } catch (SQLException | MinioException ex) {
                                log.error(ex.getMessage());
                                throw new BadRequestException(ex.getMessage());
                            }

                            assert bannerContentUpdate.getId() != null;
                            return new BannerContentUpdateDTO(
                                    UUID.fromString(bannerContentUpdate.getId()),
                                    bannercontentUpdateMap
                            );
                        })
                        .toList();
            }

            List<UUID> bannerContentUUIDRemoves = new ArrayList<>();
            if (bannerUpdateReqDTO.getContentRemoves() != null) {
                for (String contentIdStr : bannerUpdateReqDTO.getContentRemoves()) {
                    try {
                        UUID contentId = UUID.fromString(contentIdStr);
                        bannerContentUUIDRemoves.add(contentId);

                        BannerContentResDTO bannerContentResDTO = bannerDAO.findBannerContentByBannerContentId(contentId);
                        oldContentImagePathList.add(bannerContentResDTO.getContentImagePath());
                    } catch (IllegalArgumentException | SQLException ex) {
                        log.error("Error processing content remove '{}': {}", contentIdStr, ex.getMessage());
                    }
                }
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
                    bannerUpdateMap,
                    contentUpdateMap,
                    bannerContentUpdateDTO,
                    bannerContentCreateDTOs,
                    bannerContentUUIDRemoves
            );

            BannerResDTO updatedBanner = bannerDAO.updateBannerWithContentById(id, bannerUpdateDTO);

            oldContentImagePathList.forEach(oldContentImagePath -> {
                try {
                    this.minioService.deleteFile(oldContentImagePath);
                } catch (MinioException ex) {
                    log.error("error when delete old image {}", ex.getMessage());
                }
            });

            return updatedBanner;
        } catch (SQLException ex) {
            if (ex.getMessage().equals("Update failed, no rows affected")) {
                try {
                    return bannerDAO.findBannerWithContentsById(id);
                } catch (SQLException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
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
                try {
                    minioService.deleteFile(bannerResDTO.getCoverImagePath());
                } catch (MinioException ex) {
                    log.error(ex.getMessage());
                }
                for (int idx = 0; idx < bannerResDTO.getContents().size(); idx++) {
                    try {
                        minioService.deleteFile(bannerResDTO.getContents().get(idx).getContentImagePath());
                    } catch (MinioException ex) {
                        log.error(ex.getMessage());
                    }
                }
            }
            return deletedBanner;
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }
}
