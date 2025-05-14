package com.streamit.application.controllers.banner;

import com.streamit.application.dtos.banner.BannerCreateReqDTO;
import com.streamit.application.dtos.banner.BannerUpdateReqDTO;
import com.streamit.application.services.banner.BannerService;
import com.streamit.application.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/banners", produces = "application/json")
public class BannerController {
    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createBanner(
            @Validated @ModelAttribute BannerCreateReqDTO bannerCreateReqDTO
    ) {
        return new ResponseEntity<>(ResponseUtil.success("Banner created successfully.", bannerService.createBanner(bannerCreateReqDTO)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getBanners() {
        return new ResponseEntity<>(ResponseUtil.success("All banners retrieved successfully.", bannerService.getAllBanners()), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> getBannerById(@PathVariable UUID id) {
        return new ResponseEntity<>(ResponseUtil.success("Banner retrieved successfully.", bannerService.getBannerById(id)), HttpStatus.OK);
    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> updateBannerById(
            @PathVariable UUID id,
            @Validated @ModelAttribute BannerUpdateReqDTO bannerUpdateReqDTO
    ) {
        return new ResponseEntity<>(ResponseUtil.success("Banner updated successfully.", bannerService.updateBannerById(id, bannerUpdateReqDTO)), HttpStatus.OK);
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Map<String, Object>> deleteBannerById(@PathVariable UUID id) {
        bannerService.deleteBannerById(id);
        return new ResponseEntity<>(ResponseUtil.createNoDataResponse("Banner deleted successfully."), HttpStatus.OK);
    }
}
