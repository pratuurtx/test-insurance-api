package com.streamit.application.dtos.promotion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class PromotionUpdateDTO {
    private Map<String, Object> promotionUpdateMap;
    private Map<String, Object> contentUpdateMap;
}