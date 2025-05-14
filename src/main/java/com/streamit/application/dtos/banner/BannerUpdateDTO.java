package com.streamit.application.dtos.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BannerUpdateDTO {
    private Map<String, Object> bannerUpdateMap;
    private Map<String, Object> contentUpdateMap;
    private List<BannerContentUpdateDTO> bannerContentUpdateDTOs;
    private List<BannerContentCreateDTO> bannerContentCreateDTOs;
    private List<UUID> bannerContentRemoves;
}
