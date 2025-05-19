package com.streamit.application.controllers.promotion;

import com.streamit.application.dtos.promotion.PromotionCreateReqDTO;
import com.streamit.application.dtos.promotion.PromotionUpdateReqDTO;
import com.streamit.application.services.promotion.PromotionService;
import com.streamit.application.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/promotions", produces = "application/json")
public class PromotionController {

    public final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPromotions() {
        var result = this.promotionService.getPromotions();
        return new ResponseEntity<>(ResponseUtil.success("All promotions retrieved successfully.", result), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> getPromotionById(@PathVariable UUID id) {
        return new ResponseEntity<>(ResponseUtil.success("Promotion retrieved successfully.", promotionService.getPromotionById(id)), HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createPromotion(
            @Validated @ModelAttribute PromotionCreateReqDTO promotionCreateReqDTO
    ) {
        var result = promotionService.createPromotion(promotionCreateReqDTO);
        return new ResponseEntity<>(ResponseUtil.created("Promotion created successfully.", result), HttpStatus.CREATED);
    }

    @PatchMapping(path = "{id}", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> updatePromotion(
            @PathVariable("id") UUID id,
            @Validated @ModelAttribute PromotionUpdateReqDTO promotionUpdateReqDTO
    ) {
        return new ResponseEntity<>(ResponseUtil.success("Promotion updated successfully.", promotionService.updatePromotion(id, promotionUpdateReqDTO)), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> deletePromotion(@PathVariable UUID id) {
        promotionService.softDeletePromotionById(id);
        return new ResponseEntity<>(ResponseUtil.createNoDataResponse("Promotion deleted successfully."), HttpStatus.OK);
    }
}