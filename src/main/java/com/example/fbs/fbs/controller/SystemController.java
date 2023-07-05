package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.config.security.CustomUserDetailsService;
import com.example.fbs.fbs.config.security.JwtService;
import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.service.impl.UserServiceImpl;
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
        description = """
                Controller to manipulate with users in the App.
                This controller allows users to register in the application. There are separate endpoints for
                registering users with the roles CLIENT and ADMIN.
                                
                During registration, each user is assigned a unique UUID number, and their entered password 
                is encrypted using bcrypt technology. The signup process involves users entering their email 
                and password, which are validated. If the validation is successful, the password is encrypted 
                and compared with the stored password in the database. Upon successful validation, the user, 
                whether a client or an administrator, is assigned a JWT token.
                                
                Additionally, users have the ability to update their information, including their name and email."""
)
@RestController
@RequestMapping(value = "/system/")
@RequiredArgsConstructor
public class SystemController {

    private final UserServiceImpl userService;

    private final JwtService jwtService;

    private final CustomUserDetailsService customUserDetailsService;

    @Operation(summary = "create new Client in the app")
    @PostMapping("/register-client")
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

    @Operation(summary = "Possibility to update user's email and password in the app")
    @PutMapping("/{uuid}")
    public ResponseEntity<String> updateUserByUuid(@PathVariable String uuid, UserUpdateRequestDto updateRequest) {
        userService.updateUserByUuid(uuid, updateRequest);
        return ResponseEntity.ok("User updated successfully");
    }

    @Operation(summary = "create new Administrator in the app")
    @PostMapping("/register-admin")
    public ResponseEntity<String> registerAdmin(@RequestBody UserRequestDto registrationRequest) {
        String userId = userService.saveInitialAdminInfo(registrationRequest);
        return ResponseEntity.ok(userId);
    }
}
