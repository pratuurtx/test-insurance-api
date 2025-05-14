package com.streamit.application.controllers.suit_insurance;

import com.streamit.application.dtos.suit_insurance.SuitInsuranceCreateReqDTO;
import com.streamit.application.dtos.suit_insurance.SuitInsuranceUpdateReqDTO;
import com.streamit.application.services.suit_insurance.SuitInsuranceService;
import com.streamit.application.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/suit-insurances", produces = "application/json")
public class SuitInsuranceController {
    private final SuitInsuranceService suitInsuranceService;

    public SuitInsuranceController(SuitInsuranceService suitInsuranceService) {
        this.suitInsuranceService = suitInsuranceService;
    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> getSuitInsurances() {
        var result = suitInsuranceService.getAllSuitInsurances();
        return new ResponseEntity<>(ResponseUtil.success("All suit insurances retrieved successfully.", result), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> getSuitInsuranceById(@PathVariable("id") UUID id) {
        var result = suitInsuranceService.getSuitInsuranceById(id);
        return new ResponseEntity<>(ResponseUtil.success("Suit insurance retrieved successfully.", result), HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createInsurance(
            @Validated @ModelAttribute SuitInsuranceCreateReqDTO suitInsuranceReqDTO
    ) {
        var result = suitInsuranceService.createSuitInsurance(suitInsuranceReqDTO);
        return new ResponseEntity<>(ResponseUtil.created("Suit insurance created successfully.", result), HttpStatus.CREATED);
    }

    @PatchMapping(path = "{id}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updateSuitInsurance(
            @PathVariable("id") UUID id,
            @Validated @ModelAttribute SuitInsuranceUpdateReqDTO suitInsuranceUpdateReqDTO
    ) {
        System.out.println(suitInsuranceUpdateReqDTO);
        var result = this.suitInsuranceService.updateSuitInsuranceWithContentById(id, suitInsuranceUpdateReqDTO);
        return new ResponseEntity<>(ResponseUtil.success("Suit insurance updated successfully.", result), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> deleteSuitInsurance(@PathVariable("id") UUID id) {
        this.suitInsuranceService.deleteSuitInsuranceById(id);
        return new ResponseEntity<>(ResponseUtil.createNoDataResponse("Suit insurance deleted successfully."), HttpStatus.OK);
    }
}
