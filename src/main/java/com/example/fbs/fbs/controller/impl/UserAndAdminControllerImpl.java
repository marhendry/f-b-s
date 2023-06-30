package com.example.fbs.fbs.controller.impl;

import com.example.fbs.fbs.config.CustomUserDetailsService;
import com.example.fbs.fbs.config.JwtService;
import com.example.fbs.fbs.controller.UserAndAdminController;
import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.service.impl.ClientAndAdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAndAdminControllerImpl implements UserAndAdminController {

    private final ClientAndAdminServiceImpl userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public ResponseEntity<String> registerUser(UserRequestDto registrationRequest) {

        return ResponseEntity.ok(userService.saveInitialUserInfo(registrationRequest));

    }

    @Override
    public ResponseEntity<String> login(UserLoginRequestDto loginRequest) {
        boolean authenticated = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (authenticated) {
            String token = jwtService.generateToken(customUserDetailsService.loadUserByUsername(loginRequest.getEmail()));
            return ResponseEntity.ok("Login successful " + token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client with provided credentials was not found");
        }
    }

    @Override
    public ResponseEntity<String> updateUserByEmail(@PathVariable String email, UserUpdateRequestDto updateRequest) {
        userService.updateUserByEmail(email, updateRequest);
        return ResponseEntity.ok("User updated successfully");
    }

    @Override
    public ResponseEntity<String> registerAdmin(@RequestBody UserRequestDto registrationRequest) {
        String userId = userService.saveInitialAdminInfo(registrationRequest);
        return ResponseEntity.ok(userId);
    }
}
