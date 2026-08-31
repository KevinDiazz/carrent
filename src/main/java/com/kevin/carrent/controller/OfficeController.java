package com.kevin.carrent.controller;

import com.kevin.carrent.dto.OfficeCreateRequest;
import com.kevin.carrent.dto.OfficeResponse;
import com.kevin.carrent.dto.OfficeUpdateRequest;
import com.kevin.carrent.service.OfficeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<OfficeResponse>> getOffices() {

        return ResponseEntity
                .ok(officeService.getAllOffices());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfficeResponse> updateOffice(
            @PathVariable Long id,
            @Valid @RequestBody OfficeUpdateRequest request) {

        return ResponseEntity.ok(
                officeService.updateOffice(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffice(
            @PathVariable Long id) {

        officeService.deleteOffice(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}