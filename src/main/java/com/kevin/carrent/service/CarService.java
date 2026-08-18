package com.kevin.carrent.service;

import com.kevin.carrent.dto.CarCreateRequest;
import com.kevin.carrent.dto.CarResponse;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.mapper.CarMapper;
import com.kevin.carrent.repository.CarRepository;
import org.springframework.stereotype.Service;
import com.kevin.carrent.exception.CarNotFoundException;

import java.util.List;

@Service
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public CarService(CarRepository carRepository, CarMapper carMapper) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
    }

    public CarResponse createCar(CarCreateRequest request) {
        Car car = carMapper.toEntity(request);

        Car savedCar = carRepository.save(car);

        return carMapper.toResponse(savedCar);
    }

    public List<Car> getCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));
    }

    public void deleteCar(Long id) {
        carRepository.findById(id)
                .orElseThrow(() ->
                        new CarNotFoundException("Car with id " + id + " not found"));
        carRepository.deleteById(id);
    }
}
