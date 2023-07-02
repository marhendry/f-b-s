package com.example.fbs.fbs.service.impl;

import com.example.fbs.fbs.config.JwtService;
import com.example.fbs.fbs.mapper.UserMapper;
import com.example.fbs.fbs.model.dto.UserRequestDto;
import com.example.fbs.fbs.model.dto.UserUpdateRequestDto;
import com.example.fbs.fbs.model.entity.Role;
import com.example.fbs.fbs.model.entity.User;
import com.example.fbs.fbs.repository.UserRepository;
import com.example.fbs.fbs.service.ClientAndAdminService;
import com.example.fbs.fbs.utility.impl.PasswordEncoderImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientAndAdminServiceImpl implements ClientAndAdminService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoderImpl passwordEncoder;

    private final JwtService jwtService;

    @Override
    public boolean authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String encodedPassword = user.getPassword();
        return passwordEncoder.validatePassword(password, encodedPassword);
    }

    @Override
    public void updateUserByEmail(String email, UserUpdateRequestDto updateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setName(updateRequest.getName());
        user.setEmail(updateRequest.getEmail());
        userRepository.save(user);
    }

    @Override
    public String saveInitialUserInfo(UserRequestDto registrationRequest) {

        User currentUser = userRepository.findByEmail(registrationRequest.getEmail())
                .orElseGet(() -> buildUser(registrationRequest));

        setEncodedPassword(registrationRequest, currentUser);
        return userRepository.save(currentUser).getUuid();

    }

    private void setEncodedPassword(UserRequestDto registrationRequest, User currentUser) {
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        currentUser.setPassword(encodedPassword);
    }

    private User buildUser(UserRequestDto registrationRequest) {
        User currentUser = userMapper.toUserEntity(registrationRequest);
        currentUser.setUuid(UUID.randomUUID().toString());
        currentUser.setRole(Role.CLIENT);
        return currentUser;
    }

    @Override
    public String saveInitialAdminInfo(UserRequestDto registrationRequest) {
        User currentUser = userRepository.findByEmail(registrationRequest.getEmail())
                .orElseGet(() -> buildAdmin(registrationRequest));

        setEncodedPassword(registrationRequest, currentUser);
        return userRepository.save(currentUser).getUuid();
    }

    private User buildAdmin(UserRequestDto registrationRequest) {
        User currentUser = userMapper.toUserEntity(registrationRequest);
        currentUser.setUuid(UUID.randomUUID().toString());
        currentUser.setRole(Role.ADMIN);
        return currentUser;
    }
}