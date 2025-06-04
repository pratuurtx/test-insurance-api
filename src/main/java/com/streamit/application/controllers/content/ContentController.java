package com.streamit.application.controllers.content;

import com.streamit.application.dtos.common.ItemQueryParamsReqDTO;
import com.streamit.application.services.content.ContentService;
import com.streamit.application.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/contents", produces = "application/json")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> GetAllContents(@Validated @ModelAttribute ItemQueryParamsReqDTO itemQueryParams) {
        System.out.println(itemQueryParams);
        return new ResponseEntity<>(ResponseUtil.success("All contents retrieved successfully.", contentService.getAllContents(itemQueryParams)), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> GetContentById(@PathVariable UUID id) {
        return new ResponseEntity<>(ResponseUtil.success("Content retrieved successfully", contentService.getContentById(id)), HttpStatus.OK);
    }
}
