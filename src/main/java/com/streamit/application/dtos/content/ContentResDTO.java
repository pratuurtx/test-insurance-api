package com.streamit.application.dtos.content;

import com.streamit.application.dtos.common.CategoryEnum;
import com.streamit.application.dtos.common.StatusEnum;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ContentResDTO {
    private UUID id;
    private String title;
    private StatusEnum status;
    private CategoryEnum category;

    private UUID categoryContentId;
}
