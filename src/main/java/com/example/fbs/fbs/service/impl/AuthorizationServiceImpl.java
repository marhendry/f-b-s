package com.example.fbs.fbs.service.impl;//package com.example.flightbooking.service.impl;
//
////import app.system.booking.flight.config.security.JwtService;
//
//import com.example.flightbooking.mapper.UserMapper;
//import com.example.flightbooking.model.dto.UserLoginRequestDto;
//import com.example.flightbooking.model.entity.User;
//import com.example.flightbooking.repository.UserRepository;
//import com.example.flightbooking.service.AuthorizationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class AuthorizationServiceImpl implements AuthorizationService {
//
//    private final JwtService jwtService;
//    private final UserMapper userMapper;
//    private final UserRepository userRepository;
//
//    @Override
//    public String getToken(UserLoginRequestDto loginRequest) {
//        User user = userRepository.findByEmail(loginRequest.getEmail())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        Map<String, Object> claims = user.toClaims();
//
//        return jwtService.generateToken(claims, user);
//    }
//}
