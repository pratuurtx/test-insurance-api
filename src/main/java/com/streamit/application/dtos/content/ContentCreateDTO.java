package com.streamit.application.dtos.content;

import com.streamit.application.dtos.common.StatusEnum;
import com.streamit.application.dtos.common.TypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ContentCreateDTO {
    private String title;
    private StatusEnum status;
    private TypeEnum type;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
}
