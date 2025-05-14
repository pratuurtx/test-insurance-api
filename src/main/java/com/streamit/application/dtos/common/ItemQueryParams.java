package com.streamit.application.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemQueryParams {
    private Integer page;
    private Integer pageSize;

    private String status;
    private String category;
}
