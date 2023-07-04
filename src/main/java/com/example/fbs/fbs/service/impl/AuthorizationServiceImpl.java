package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorizationServiceImpl implements AuthorizationService {
    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    public String generateToken(UserLoginRequestDto loginRequestDto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getEmail());

        log.info("Generating token for user: {}", userDetails.getUsername());

        return jwtService.generateToken(userDetails);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        return jwtService.validateToken(token, userDetails);
    }
}
