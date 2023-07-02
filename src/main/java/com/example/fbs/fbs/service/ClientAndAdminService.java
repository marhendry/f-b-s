package com.example.fbs.fbs.service;

import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ClientAndAdminService {
//    User registerUser(UserRequestDto registrationRequest);

    String saveInitialUserInfo(UserRequestDto registrationRequest);

    boolean authenticate(String email, String password);

    void updateUserByEmail(String email, UserUpdateRequestDto updateRequest);

    String saveInitialAdminInfo(UserRequestDto registrationRequest);
}