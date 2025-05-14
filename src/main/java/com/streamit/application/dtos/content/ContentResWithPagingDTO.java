package com.streamit.application.dtos.content;

import com.streamit.application.dtos.common.Paging;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ContentResWithPagingDTO {
    private List<ContentResDTO> content;
    private Paging paging;
}
