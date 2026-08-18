package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.CarCreateRequest;
import com.kevin.carrent.dto.CarResponse;
import com.kevin.carrent.dto.CarUpdateRequest;
import com.kevin.carrent.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarMapper {
    Car toEntity(CarCreateRequest request);
    CarResponse toResponse(Car car);
    void updateEntity(CarUpdateRequest request, @MappingTarget Car car);
}
