package com.example.fbs.fbs.service;

import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;

public interface UserService {

    String saveInitialUserInfo(UserRequestDto registrationRequest);

    boolean authenticate(String email, String password);

    void updateUserByEmail(String email, UserUpdateRequestDto updateRequest);

    String saveInitialAdminInfo(UserRequestDto registrationRequest);
}