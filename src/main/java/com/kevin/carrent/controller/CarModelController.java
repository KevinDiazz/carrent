package com.kevin.carrent.controller;

import com.kevin.carrent.dto.*;
import com.kevin.carrent.service.CarModelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car-models")
public class CarModelController {

    private final CarModelService carModelService;

    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }

    @PostMapping()
    public ResponseEntity<CarModelResponse> createCarModel(
            @Valid @RequestBody CarModelCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carModelService.createCarModel(request));
    }

    @GetMapping
    public ResponseEntity<List<CarModelResponse>> getAllCarModels() {
        return ResponseEntity
                .ok(carModelService.getAllCarModels());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarModelResponse> updateCarModel(
            @PathVariable Long id,
            @Valid @RequestBody CarModelUpdateRequest request) {

        return ResponseEntity.ok(carModelService.updateCarModel(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarModel(
            @PathVariable Long id) {

        carModelService.deleteCarModel(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}