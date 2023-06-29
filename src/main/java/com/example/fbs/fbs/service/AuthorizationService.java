package com.example.fbs.fbs.service;


import com.example.fbs.fbs.model.dto.UserLoginRequestDto;

public interface AuthorizationService {
    String getToken(UserLoginRequestDto loginRequest);

}
