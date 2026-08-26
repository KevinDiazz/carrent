package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.CarCreateRequest;
import com.kevin.carrent.dto.CarResponse;
import com.kevin.carrent.dto.CarUpdateRequest;
import com.kevin.carrent.entity.Car;
import com.kevin.carrent.entity.CarModel;
import com.kevin.carrent.entity.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(target = "carModel", source = "carModel")
    @Mapping(target = "office", source = "office")
    Car toEntity(
            CarCreateRequest request,
            CarModel carModel,
            Office office
    );

    @Mapping(target = "carModelId", source = "carModel.id")
    @Mapping(target = "brand", source = "carModel.brand")
    @Mapping(target = "model", source = "carModel.model")
    @Mapping(target = "officeId", source = "office.id")
    @Mapping(target = "officeName", source = "office.name")
    CarResponse toResponse(Car car);

    @Mapping(target = "carModel", source = "carModel")
    @Mapping(target = "office", source = "office")
    Car updateEntity(
            CarUpdateRequest request,
            @MappingTarget Car car,
            CarModel carModel,
            Office office
    );
}