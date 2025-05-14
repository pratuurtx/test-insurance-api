package com.streamit.application.services.content;

import com.streamit.application.daos.content.ContentDAO;
import com.streamit.application.dtos.common.*;
import com.streamit.application.dtos.content.ContentResDTO;
import com.streamit.application.dtos.content.ContentResWithPagingDTO;
import com.streamit.application.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class ContentService {
    private final ContentDAO contentDAO;

    public ContentService(ContentDAO contentDAO) {
        this.contentDAO = contentDAO;
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
}
