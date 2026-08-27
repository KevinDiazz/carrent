package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.CarModelCreateRequest;
import com.kevin.carrent.dto.CarModelResponse;
import com.kevin.carrent.dto.CarModelUpdateRequest;
import com.kevin.carrent.entity.CarModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarModelMapper {

    CarModel toEntity(CarModelCreateRequest request);

    CarModelResponse toResponse(CarModel carModel);

    CarModel updateEntity(
            CarModelUpdateRequest request,
            @MappingTarget CarModel carModel
    );
}