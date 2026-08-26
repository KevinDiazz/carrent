package com.kevin.carrent.controller;

import com.kevin.carrent.dto.OfficeCreateRequest;
import com.kevin.carrent.dto.OfficeResponse;
import com.kevin.carrent.service.OfficeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/offices")
public class OfficeController {

    private final OfficeService officeService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @PostMapping
    public ResponseEntity<OfficeResponse> createOffice(
            @Valid @RequestBody OfficeCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(officeService.createOffice(request));
    }
}