package com.streamit.application.dtos.suit_insurance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class SuitInsuranceUpdateDTO {
    private Map<String, Object> suitInsuranceUpdateMap;
    private Map<String, Object> contentUpdateMap;
}
