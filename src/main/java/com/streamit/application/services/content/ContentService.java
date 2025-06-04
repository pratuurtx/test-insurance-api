package com.streamit.application.services.content;

import com.streamit.application.daos.banner.BannerDAO;
import com.streamit.application.daos.content.ContentDAO;
import com.streamit.application.dtos.banner.BannerResDTO;
import com.streamit.application.dtos.common.*;
import com.streamit.application.dtos.content.ContentResDTO;
import com.streamit.application.dtos.content.ContentResWithPagingDTO;
import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.services.banner.BannerService;
import com.streamit.application.services.insurance.InsuranceService;
import com.streamit.application.services.promotion.PromotionService;
import com.streamit.application.services.suit_insurance.SuitInsuranceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ContentService {
    private final ContentDAO contentDAO;
    private final BannerService bannerService;
    private final PromotionService promotionService;
    private final InsuranceService insuranceService;
    private final SuitInsuranceService suitInsuranceService;

    public ContentService(ContentDAO contentDAO, BannerService bannerService, PromotionService promotionService, InsuranceService insuranceService, SuitInsuranceService suitInsuranceService) {
        this.contentDAO = contentDAO;
        this.bannerService = bannerService;
        this.promotionService = promotionService;
        this.insuranceService = insuranceService;
        this.suitInsuranceService = suitInsuranceService;
    }

    public ContentResWithPagingDTO getAllContents(ItemQueryParamsReqDTO itemQueryParamsReqDTO) {
        try {
            ItemQueryParams itemQueryParams = new ItemQueryParams(
                    Integer.parseInt(itemQueryParamsReqDTO.getPage()),
                    Integer.parseInt(itemQueryParamsReqDTO.getPageSize()),
                    itemQueryParamsReqDTO.getStatus(),
                    itemQueryParamsReqDTO.getCategory()
            );
            List<ContentResDTO> contents = contentDAO.findAllContentsWithItemQueryParams(itemQueryParams);
            int totalRow = contentDAO.getTotalCount(itemQueryParams);
            int totalPage = (int) Math.ceil((double) totalRow / itemQueryParams.getPageSize());
            return new ContentResWithPagingDTO(
                    contents,
                    new Paging(
                            itemQueryParams.getPage(),
                            itemQueryParams.getPageSize(),
                            List.of(5, 10, 20, 50, 100),
                            totalPage,
                            totalRow
                    )
            );
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }

    public Object getContentById(UUID id) {
        try {
            ContentResDTO content = contentDAO.findContentCategoryById(id).orElseThrow(() -> new BadRequestException("Content was not found."));
            System.out.println("Category" + content.getCategory());
            System.out.println("ID" + content.getCategoryContentId());
            if (content.getCategory() == CategoryEnum.BANNER) {
                return bannerService.getBannerById(content.getCategoryContentId());
            } else if (content.getCategory() == CategoryEnum.PROMOTION) {
                return promotionService.getPromotionById(content.getCategoryContentId());
            } else if (content.getCategory() == CategoryEnum.INSURANCE) {
                return insuranceService.getInsuranceById(content.getCategoryContentId());
            } else if (content.getCategory() == CategoryEnum.SUIT_INSURANCE) {
                return suitInsuranceService.getSuitInsuranceById(content.getCategoryContentId());
            }
            throw new BadRequestException("Content was not found.");
        } catch (SQLException ex) {
            log.error(ex.getMessage());
            throw new BadRequestException(ex.getMessage());
        }
    }
}
