package com.kevin.carrent.service;

import com.kevin.carrent.dto.CarModelCreateRequest;
import com.kevin.carrent.dto.CarModelResponse;
import com.kevin.carrent.entity.CarModel;
import com.kevin.carrent.mapper.CarModelMapper;
import com.kevin.carrent.repository.CarModelRepository;
import org.springframework.stereotype.Service;

@Service
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarModelMapper carModelMapper;

    public CarModelService(
            CarModelRepository carModelRepository,
            CarModelMapper carModelMapper) {

        this.carModelRepository = carModelRepository;
        this.carModelMapper = carModelMapper;
    }

    public CarModelResponse createCarModel(CarModelCreateRequest request) {

        CarModel carModel = carModelMapper.toEntity(request);

        CarModel savedCarModel = carModelRepository.save(carModel);

        return carModelMapper.toResponse(savedCarModel);
    }
}