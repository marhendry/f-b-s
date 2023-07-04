package com.example.fbs.fbs.service;

import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthorizationService {
    String generateToken(UserLoginRequestDto loginRequestDto);
    boolean validateToken(String token, UserDetails userDetails);

}
