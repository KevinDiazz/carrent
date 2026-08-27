package com.kevin.carrent.service;

import com.kevin.carrent.dto.CarModelCreateRequest;
import com.kevin.carrent.dto.CarModelResponse;
import com.kevin.carrent.dto.CarModelUpdateRequest;
import com.kevin.carrent.entity.CarModel;
import com.kevin.carrent.exception.CarModelInUseException;
import com.kevin.carrent.exception.CarModelNotFoundException;
import com.kevin.carrent.mapper.CarModelMapper;
import com.kevin.carrent.repository.CarModelRepository;
import com.kevin.carrent.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarModelMapper carModelMapper;
    private final CarRepository carRepository;

    public CarModelService(
            CarModelRepository carModelRepository,
            CarModelMapper carModelMapper, CarRepository carRepository) {

        this.carModelRepository = carModelRepository;
        this.carModelMapper = carModelMapper;
        this.carRepository = carRepository;
    }

    public CarModelResponse createCarModel(CarModelCreateRequest request) {

        CarModel carModel = carModelMapper.toEntity(request);

        CarModel savedCarModel = carModelRepository.save(carModel);

        return carModelMapper.toResponse(savedCarModel);
    }

    public List<CarModelResponse> getAllCarModels() {
        List<CarModel> carModels = carModelRepository.findAll();
        return carModels.stream().map(carModelMapper::toResponse).toList();
    }

    public CarModelResponse updateCarModel(
            Long id,
            CarModelUpdateRequest request) {

        CarModel carModel = carModelRepository.findById(id)
                .orElseThrow(() ->
                        new CarModelNotFoundException(
                                "Car model with id " + id + " not found"
                        ));

        carModelMapper.updateEntity(request, carModel);

        CarModel updatedCarModel = carModelRepository.save(carModel);

        return carModelMapper.toResponse(updatedCarModel);
    }

    public void deleteCarModel(Long id) {

        carModelRepository.findById(id)
                .orElseThrow(() ->
                        new CarModelNotFoundException(
                                "Car model with id " + id + " not found"
                        ));

        if (carRepository.existsByCarModelId(id)) {
            throw new CarModelInUseException(
                    "Car model with id " + id + " is currently assigned to cars"
            );
        }

        carModelRepository.deleteById(id);
    }
}