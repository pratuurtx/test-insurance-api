package com.streamit.application.controllers.attachment;

import com.streamit.application.exceptions.BadRequestException;
import com.streamit.application.services.minio.MinioService;
import io.minio.errors.MinioException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/attachments")
public class AttachmentController {

    private final MinioService minioService;

    public AttachmentController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping(path = "/{fileName:.+}")
    public ResponseEntity<InputStreamResource> serveFile(
            @PathVariable String fileName
    ) {
        try {
            String contentType = minioService.getContentType(fileName);
            System.out.println("contentType: " + contentType);
            if (!contentType.startsWith("image/")) {
                throw new BadRequestException("Only image files can be previewed");
            }

            InputStream inputStream = minioService.getFileAsStream(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .body(new InputStreamResource(inputStream));
        } catch (MinioException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}
