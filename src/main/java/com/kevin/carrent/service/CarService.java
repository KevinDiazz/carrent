package com.kevin.carrent.service;

import com.kevin.carrent.dto.CarCreateRequest;
import com.kevin.carrent.dto.CarResponse;
import com.kevin.carrent.dto.CarUpdateRequest;
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

    public List<CarResponse> getCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(carMapper::toResponse)
                .toList();
    }

    public CarResponse getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException("Car with id " + id + " not found"));
        return carMapper.toResponse(car);
    }

    public void deleteCar(Long id) {
        carRepository.findById(id)
                .orElseThrow(() ->
                        new CarNotFoundException("Car with id " + id + " not found"));
        carRepository.deleteById(id);
    }

    public CarResponse updateCar(Long id, CarUpdateRequest request) {

        Car car = carRepository.findById(id)
                .orElseThrow(() ->
                        new CarNotFoundException("Car with id " + id + " not found"));

        carMapper.updateEntity(request, car);

        Car updatedCar = carRepository.save(car);

        return carMapper.toResponse(updatedCar);
    }
}
