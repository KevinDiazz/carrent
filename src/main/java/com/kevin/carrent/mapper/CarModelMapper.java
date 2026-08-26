package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.CarModelCreateRequest;
import com.kevin.carrent.dto.CarModelResponse;
import com.kevin.carrent.entity.CarModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarModelMapper {

    CarModel toEntity(CarModelCreateRequest request);

    CarModelResponse toResponse(CarModel carModel);
}