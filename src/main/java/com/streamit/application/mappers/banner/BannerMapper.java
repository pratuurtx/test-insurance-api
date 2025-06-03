package com.streamit.application.mappers.banner;

import com.streamit.application.dtos.banner.*;
import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.content.Content;

import java.util.List;
import java.util.UUID;

public class BannerMapper {
    public static BannerResDTO mapBannerToBannerResDTO(
            Banner banner,
            List<BannerContent> bannerContents,
            Content content
    ) {
        return new BannerResDTO(
                banner.getId(),
                
                content.getTitle(),
                content.getEffectiveFrom(),
                content.getEffectiveTo(),
                content.getStatus(),
                CategoryEnum.BANNER,

                banner.getCoverImagePath(),
                banner.getCoverHyperLink(),
                BannerMapper.mapBannerContentListToBannerContentResDTOList(bannerContents)
        );
    }

    public static List<BannerContentResDTO> mapBannerContentListToBannerContentResDTOList(List<BannerContent> bannerContents) {
        return bannerContents.stream()
                .map(BannerMapper::mapBannerContentToBannerContentResDTO)
                .toList();
    }

    public static BannerContentResDTO mapBannerContentToBannerContentResDTO(BannerContent bannerContent) {
        return new BannerContentResDTO(
                bannerContent.getId(),
                bannerContent.getContentImagePath(),
                bannerContent.getContentHyperLink()
        );
    }

    public static List<BannerContentCreateWithBannerDTO> mapBannerContentCreateDTOListToBannerCreateWithContentDTOList(UUID bannerId, List<BannerContentCreateDTO> bannerContentCreateDTOs) {
        return bannerContentCreateDTOs.stream()
                .map(bannerContentCreateDTO -> BannerMapper.mapBannerContentCreateDTOToBannerCreateWithContent(bannerId, bannerContentCreateDTO))
                .toList();
    }

    public static BannerContentCreateWithBannerDTO mapBannerContentCreateDTOToBannerCreateWithContent(UUID bannerId, BannerContentCreateDTO bannerContentCreateDTO) {
        return new BannerContentCreateWithBannerDTO(
                bannerId,
                bannerContentCreateDTO.getContentImagePath(),
                bannerContentCreateDTO.getContentHyperLink()
        );
    }

    public static BannerCreateWithContentDTO mapBannerCreateDTOToBannerCreateWithContent(UUID contentId, BannerCreateDTO bannerCreateDTO) {
        return new BannerCreateWithContentDTO(
                bannerCreateDTO.getCoverImagePath(),
                bannerCreateDTO.getCoverHyperLink(),
                contentId
        );
    }
}
