package com.example.fbs.fbs.mapper;

import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.entity.Role;
import com.example.fbs.fbs.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserRequestDto userRequestDto, String uuid, Role role) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .uuid(uuid)
                .role(role)
                .build();
    }
}