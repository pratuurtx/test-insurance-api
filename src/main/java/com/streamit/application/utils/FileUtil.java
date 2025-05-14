package com.streamit.application.utils;

import com.streamit.application.dtos.common.FileNameResponseDTO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

public class FileUtil {
    public static FileNameResponseDTO getFileDetail(String fileName) {
        if (fileName != null && fileName.lastIndexOf('.') > 0) {
            return new FileNameResponseDTO(
                    FilenameUtils.getBaseName(fileName),
                    FilenameUtils.getExtension(fileName)
            );
        }
        return new FileNameResponseDTO(
                fileName != null ? fileName : "unnamed",
                ""
        );
    }

    public static String generateFileName(String fileName) {
        String originalFileName = StringUtils.cleanPath(fileName);
        var fileDetail = getFileDetail(originalFileName);

        boolean hasExtension = StringUtils.hasText(fileDetail.getExtension());
        String extensionSuffix = hasExtension ? "." + fileDetail.getExtension() : "";

        return String.format("%s_%d_%s%s",
                fileDetail.getBaseName(),
                Instant.now().getEpochSecond(),
                UUID.randomUUID().toString().substring(0, 8),
                extensionSuffix
        );
    }
}
