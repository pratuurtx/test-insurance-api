package com.streamit.application.dtos.banner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BannerContentUpdateDTO {
    private UUID id;
    private Map<String, Object> contentFieldMap;
}
