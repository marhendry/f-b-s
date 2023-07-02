package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.CustomUserDetailsService;
import com.example.fbs.fbs.config.JwtService;
import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.service.impl.ClientAndAdminServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "User and Admin controller",
        description = "Controller to manipulate with users in the App."
)
@RestController
@RequestMapping(value = "/system/")
@RequiredArgsConstructor
public class UserAndAdminControllerImpl {

    private final ClientAndAdminServiceImpl userService;

    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;

    @Operation(summary = "create new User in the app")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(UserRequestDto registrationRequest) {

        return ResponseEntity.ok(userService.saveInitialUserInfo(registrationRequest));

    }

    @Operation(summary = "login by the User in the app")
    @PostMapping("/login")
    public ResponseEntity<String> login(UserLoginRequestDto loginRequest) {
        boolean authenticated = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (authenticated) {
            String token = jwtService.generateToken(customUserDetailsService.loadUserByUsername(loginRequest.getEmail()));
            return ResponseEntity.ok("Login successful " + token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client with provided credentials was not found");
        }
    }

    @PutMapping("/{email}")
    public ResponseEntity<String> updateUserByEmail(@PathVariable String email, UserUpdateRequestDto updateRequest) {
        userService.updateUserByEmail(email, updateRequest);
        return ResponseEntity.ok("User updated successfully");
    }

    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody UserRequestDto registrationRequest) {
        String userId = userService.saveInitialAdminInfo(registrationRequest);
        return ResponseEntity.ok(userId);
    }
}
