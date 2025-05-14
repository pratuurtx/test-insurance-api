package com.streamit.application.dtos.banner;

import com.streamit.application.dtos.common.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Banner {
    private UUID id;
    private String coverImagePath;
    private String coverHyperLink;
    private UUID contentId;
}
