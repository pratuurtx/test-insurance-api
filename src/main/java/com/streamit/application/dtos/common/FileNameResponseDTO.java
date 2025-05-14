package com.streamit.application.dtos.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileNameResponseDTO {
    private String baseName;
    private String extension;
}
