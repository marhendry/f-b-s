package com.example.fbs.fbs.controller;

import com.example.fbs.fbs.model.dto.UserLoginRequestDto;
import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
        name = "User and Admin controller",
        description = "Controller to manipulate with users in the App."
)
@RequestMapping(value = "/system/")
public interface UserAndAdminController {

    @Operation(summary = "create new User in the app")
    @PostMapping("/register")
    ResponseEntity<String> registerUser(@RequestBody UserRequestDto registrationRequest);

    @Operation(summary = "login by the User in the app")
    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody UserLoginRequestDto loginRequest);

    @PutMapping("/{email}")
    ResponseEntity<String> updateUserByEmail(@PathVariable String email, @RequestBody UserUpdateRequestDto updateRequest);

    @PostMapping("/register-admin")
    ResponseEntity<String> registerAdmin(@RequestBody UserRequestDto registrationRequest);
}