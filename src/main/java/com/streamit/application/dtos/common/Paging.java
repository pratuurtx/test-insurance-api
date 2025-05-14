package com.streamit.application.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Paging {
    private Integer pageNo;
    private Integer pageSize;
    private List<Integer> rowsPerPageOption;
    private Integer totalPage;
    private Integer totalRow;
}
