package com.kevin.carrent.mapper;

import com.kevin.carrent.dto.RegisterRequest;
import com.kevin.carrent.dto.RegisterResponse;
import com.kevin.carrent.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequest request);
    RegisterResponse toResponse(User user);
}
