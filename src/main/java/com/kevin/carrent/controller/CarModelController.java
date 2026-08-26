package com.kevin.carrent.controller;

import com.kevin.carrent.dto.CarModelCreateRequest;
import com.kevin.carrent.dto.CarModelResponse;
import com.kevin.carrent.service.CarModelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}