package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.OfficeCreateRequest;
import com.kevin.carrent.dto.OfficeResponse;
import com.kevin.carrent.entity.Office;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfficeMapper {

    Office toEntity(OfficeCreateRequest request);

    OfficeResponse toResponse(Office office);
}