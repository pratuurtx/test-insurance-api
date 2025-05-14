package com.streamit.application.dtos.insurance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class InsuranceUpdateDTO {
    private Map<String, Object> insuranceUpdateMap;
    private Map<String, Object> contentUpdateMap;
}
