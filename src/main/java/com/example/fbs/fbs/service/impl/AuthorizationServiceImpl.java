package com.example.fbs.fbs.service.impl;//package com.example.flightbooking.service.impl;

//import app.system.booking.flight.config.security.JwtService;

import com.example.fbs.fbs.config.CustomUserDetails;
import com.example.fbs.fbs.config.JwtService;
import com.example.fbs.fbs.mapper.UserMapper;
import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final JwtService jwtService;

private final UserDetailsService userDetailsService;
    @Override
    public String generateToken(UserLoginRequestDto loginRequestDto) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDto.getEmail());
        return jwtService.generateToken(userDetails);

    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        // Проверка подлинности и целостности токена
        return jwtService.validateToken(token, userDetails);
    }

//    @Override
//    public UserDetails getUserDetailsFromToken(String token) {
//        // Извлечение данных пользователя из токена
//        String username = jwtService.extractUsername(token);
//        // Загрузка дополнительной информации о пользователе из базы данных или другого источника
//        User user = userRepository.findByUuid(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Создание UserDetails объекта для использования в системе авторизации Spring Security
//        return new UserDetailsImpl(user);
//    }
}
