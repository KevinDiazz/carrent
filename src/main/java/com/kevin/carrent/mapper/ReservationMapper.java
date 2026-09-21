package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.ReservationResponse;
import com.kevin.carrent.entity.Reservations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "licensePlate", source = "car.licensePlate")
    @Mapping(target = "brand", source = "car.carModel.brand")
    @Mapping(target = "model", source = "car.carModel.model")
    @Mapping(target = "officeName", source = "car.office.name")
    @Mapping(target = "officeId", source = "car.office.id")
    @Mapping(target = "officeAddress", source = "car.office.address")
    @Mapping(target = "officeCity", source = "car.office.city")
    @Mapping(target = "officePhone", source = "car.office.phone")
    ReservationResponse toResponse(Reservations reservation);
}