package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.OfficeCreateRequest;
import com.kevin.carrent.dto.OfficeResponse;
import com.kevin.carrent.dto.OfficeUpdateRequest;
import com.kevin.carrent.entity.Office;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OfficeMapper {

    Office toEntity(OfficeCreateRequest request);

    OfficeResponse toResponse(Office office);

    void updateEntity(
            OfficeUpdateRequest request,
            @MappingTarget Office office
    );
}