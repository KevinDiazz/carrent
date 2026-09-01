package com.kevin.carrent.controller;

import com.kevin.carrent.dto.*;
import com.kevin.carrent.enums.CarStatus;
import com.kevin.carrent.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getCars(
            @ModelAttribute CarFilterRequest filter) {
        return ResponseEntity.ok(
                carService.getCars(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<CarResponse> createCar(
            @Valid @RequestBody CarCreateRequest car) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carService.createCar(car));
    }

    @GetMapping("/available")
    public ResponseEntity<List<CarAvailabilityResponse>> getAvailableCars(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Long officeId) {

        return ResponseEntity.ok(
                carService.getAvailableCars(
                        startDate,
                        endDate,
                        officeId
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarUpdateRequest request) {
        return ResponseEntity.ok(carService.updateCar(id, request));
    }

}
