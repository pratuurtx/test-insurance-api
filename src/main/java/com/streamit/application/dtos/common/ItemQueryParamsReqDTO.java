package com.streamit.application.dtos.common;

import com.streamit.application.annotations.common.ValidItemQueryContent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@ToString
@ValidItemQueryContent
public class ItemQueryParamsReqDTO {
    private String page;
    private String pageSize;

    private String status;
    private String category;

    public void applyDefault(ItemQueryParamsReqDTO itemQueryParamsReqDTO) {
        try {
            if (Integer.parseInt(itemQueryParamsReqDTO.getPage()) < 0) {
                itemQueryParamsReqDTO.setPage("0");
            }
        } catch (NumberFormatException ex) {
            log.error("page: {}", ex.getMessage());
            itemQueryParamsReqDTO.setPage("0");
        }

        try {
            int pageInt = Integer.parseInt(itemQueryParamsReqDTO.getPageSize());
            if (pageInt < 5) {
                itemQueryParamsReqDTO.setPageSize("5");
            } else {
                if (pageInt > 50) {
                    itemQueryParamsReqDTO.setPageSize("100");
                } else if (pageInt > 20) {
                    itemQueryParamsReqDTO.setPageSize("50");
                } else if (pageInt > 10) {
                    itemQueryParamsReqDTO.setPageSize("20");
                } else if (pageInt > 5) {
                    itemQueryParamsReqDTO.setPageSize("10");
                }
            }
        } catch (NumberFormatException ex) {
            log.error("pageSize: {}", ex.getMessage());
            itemQueryParamsReqDTO.setPageSize("5");
        }

        if (itemQueryParamsReqDTO.getStatus() != null) {
            try {
                StatusEnum.valueOf(itemQueryParamsReqDTO.getStatus().toUpperCase());
                itemQueryParamsReqDTO.setStatus(itemQueryParamsReqDTO.getStatus().toUpperCase());
            } catch (IllegalArgumentException ex) {
                log.error("status: {}", ex.getMessage());
                itemQueryParamsReqDTO.setStatus(null);
            }
        }

        if (itemQueryParamsReqDTO.getCategory() != null) {
            try {
                CategoryEnum.valueOf(itemQueryParamsReqDTO.getCategory().toUpperCase());
                itemQueryParamsReqDTO.setCategory(itemQueryParamsReqDTO.getCategory().toUpperCase());
            } catch (IllegalArgumentException ex) {
                log.error("category: {}", ex.getMessage());
                itemQueryParamsReqDTO.setCategory(null);
            }
        }
    }
}