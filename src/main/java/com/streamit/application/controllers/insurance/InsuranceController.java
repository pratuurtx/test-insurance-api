package com.streamit.application.controllers.insurance;

import com.streamit.application.dtos.insurance.InsuranceCreateReqDTO;
import com.streamit.application.dtos.insurance.InsuranceUpdateReqDTO;
import com.streamit.application.services.insurance.InsuranceService;
import com.streamit.application.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/insurances", produces = "application/json")
public class InsuranceController {

    public final InsuranceService insuranceService;

    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getInsurances() {
        var result = this.insuranceService.getInsurances();
        return new ResponseEntity<>(ResponseUtil.success("All insurances retrieved successfully.", result), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> getInsuranceById(@PathVariable UUID id) {
        return new ResponseEntity<>(ResponseUtil.success("Insurance retrieved successfully.", insuranceService.getInsuranceById(id)), HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createInsurance(
            @Validated @ModelAttribute InsuranceCreateReqDTO insuranceCreateReqDTO
    ) {
        var result = insuranceService.createInsurance(insuranceCreateReqDTO);
        return new ResponseEntity<>(ResponseUtil.created("Insurance created successfully.", result), HttpStatus.CREATED);
    }

    @PatchMapping(path = "{id}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateInsurance(
            @PathVariable("id") UUID id,
            @Validated @ModelAttribute InsuranceUpdateReqDTO insuranceUpdateReqDTO
    ) {
        return new ResponseEntity<>(ResponseUtil.success("Insurance updated successfully.", insuranceService.updateInsurance(id, insuranceUpdateReqDTO)), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> deleteInsurance(@PathVariable UUID id) {
        insuranceService.softDeleteInsuranceById(id);
        return new ResponseEntity<>(ResponseUtil.createNoDataResponse("Insurance deleted successfully."), HttpStatus.OK);
    }
}
