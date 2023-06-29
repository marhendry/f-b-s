package com.example.fbs.fbs.mapper;

import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.email", target = "email")
    @Mapping(source = "dto.password", target = "password")
    User toUserEntity(UserRequestDto dto);

}