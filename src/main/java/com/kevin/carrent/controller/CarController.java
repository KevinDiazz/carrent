package com.kevin.carrent.controller;

import com.kevin.carrent.dto.CarCreateRequest;
import com.kevin.carrent.dto.CarResponse;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getCars() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(carService.getCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping
    public ResponseEntity<CarResponse> createCar(@RequestBody CarCreateRequest car) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carService.createCar(car));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
