package com.streamit.application.controllers.content;

import com.streamit.application.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/contents", consumes = "application/json")
public class ContentController {

    public ResponseEntity<Map<String, Object>> GetAllContents() {
        return new ResponseEntity<>(ResponseUtil.success("All contents retrieved successfully.", null), HttpStatus.OK);
    }
}
